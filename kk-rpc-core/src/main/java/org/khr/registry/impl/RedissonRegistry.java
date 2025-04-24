package org.khr.registry.impl;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import org.khr.config.RegistryConfig;
import org.khr.model.ServiceMetaInfo;
import org.khr.registry.Registry;
import org.khr.registry.RegistryServiceCache;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 Redisson 的注册中心实现
 */
public class RedissonRegistry implements Registry {

    private RedissonClient redissonClient;
    private RMap<String, String> serviceMap;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 根节点
     */
    private static final String REDIS_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        // 创建 Redisson 客户端并初始化
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");  // 配置 Redis 地址
        redissonClient = Redisson.create(config);

        // 获取 RMap 来存储服务信息
        serviceMap = redissonClient.getMap("serviceMap");

        // 启动心跳续签任务
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registerKey = REDIS_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        String value = JSONUtil.toJsonStr(serviceMetaInfo);

        // 将服务注册到 Redis 中
        serviceMap.put(registerKey, value);

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = REDIS_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        serviceMap.remove(registerKey);  // 从 Redis 中删除

        // 从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索
        String searchPrefix = REDIS_ROOT_PATH + serviceKey + "/";
        List<ServiceMetaInfo> serviceMetaInfoList = serviceMap.keySet().stream()
                .filter(key -> key.startsWith(searchPrefix))
                .map(key -> {
                    String value = serviceMap.get(key);
                    return JSONUtil.toBean(value, ServiceMetaInfo.class);
                })
                .collect(Collectors.toList());

        // 写入服务缓存
        registryServiceCache.writeCache(serviceMetaInfoList);
        return serviceMetaInfoList;
    }

    @Override
    public void heartBeat() {
        // 每 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            // 遍历本节点所有的 key
            for (String key : localRegisterNodeKeySet) {
                try {
                    String value = serviceMap.get(key);
                    if (value == null) {
                        continue;
                    }
                    // 节点未过期，重新注册（相当于续签）
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(key + "续签失败", e);
                }
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        RMap<String, String> serviceMap = redissonClient.getMap("serviceMap");
        // 监听条目删除事件（只针对 serviceNodeKey）
    }


    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 下线节点
        for (String key : localRegisterNodeKeySet) {
            try {
                serviceMap.remove(key);  // 从 Redis 中删除
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }

        // 释放资源
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }
}
