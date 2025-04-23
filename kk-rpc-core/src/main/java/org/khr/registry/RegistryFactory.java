package org.khr.registry;


import org.khr.registry.impl.EtcdRegistry;
import org.khr.spi.SpiLoader;

/**
 * 注册中心工厂（用于获取注册中心对象）
 */
public class RegistryFactory {

    // SPI 动态加载
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
