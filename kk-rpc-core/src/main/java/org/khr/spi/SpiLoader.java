package org.khr.spi;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器
 * 自定义实现，支持键值对映射（自动扫描实现类）
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名 =>（key => 实现类）
     */
    public static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();


    /**
     * 获取某个接口的实例
     *
     * @param tClass 接口类
     * @param key    实现类 key
     * @param <T>    泛型返回
     * @return 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        // 实例缓存机制
        return (T) instanceCache.computeIfAbsent(implClassName, name -> {
            try {
                return implClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(String.format("%s 类实例化失败", implClassName), e);
            }
        });
    }

    /**
     * 加载某个接口的所有实现类（自动扫描）
     *
     * @param loadClass 接口类型
     * @return 实现类集合
     */
    @SuppressWarnings("unchecked")
    public static Set<Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("") // 空字符串表示扫描所有包
                .addClassLoaders(Thread.currentThread().getContextClassLoader())
        );

        // 获取所有实现类
        Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) loadClass);

        // 构造 key => 实现类映射（key 默认用 simpleName 的小写）
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (Class<?> implClass : subTypes) {
            if (implClass.isInterface() || implClass.isAnnotation() || implClass.isEnum()) continue;

            String key = implClass.getSimpleName();
            // 若 key 已存在，可按需定义覆盖逻辑，这里简单直接覆盖
            keyClassMap.put(key, implClass);
        }

        loaderMap.put(loadClass.getName(), keyClassMap);
        log.info("加载到 {} 接口类型的实现类 {}", loadClass.getName(), keyClassMap.keySet());
        return subTypes;
    }

}
