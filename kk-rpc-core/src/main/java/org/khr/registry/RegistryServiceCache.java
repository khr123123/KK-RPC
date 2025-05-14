package org.khr.registry;

import org.khr.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存（支持多服务缓存）
 */
public class RegistryServiceCache {

    /**
     * 多服务缓存：serviceKey -> 对应的服务列表
     */
    private final Map<String, List<ServiceMetaInfo>> serviceCacheMap = new ConcurrentHashMap<>();

    /**
     * 写入缓存
     *
     * @param serviceKey      服务键
     * @param newServiceCache 新的服务缓存列表
     */
    public void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        serviceCacheMap.put(serviceKey, newServiceCache);
    }

    /**
     * 读取缓存
     *
     * @param serviceKey 服务键
     * @return 对应的服务缓存列表（可能为 null）
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        return serviceCacheMap.get(serviceKey);
    }

    /**
     * 清除指定服务的缓存
     *
     * @param serviceKey 服务键
     */
    public void clearCache(String serviceKey) {
        serviceCacheMap.remove(serviceKey);
    }

    /**
     * 清除所有服务的缓存
     */
    public void clearAllCache() {
        serviceCacheMap.clear();
    }
}
