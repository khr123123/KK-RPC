package org.khr.serializer;


import org.khr.serializer.impl.JdkSerializer;
import org.khr.spi.SpiLoader;

import java.util.List;
import java.util.Map;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象）
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        if (key == null || key.isEmpty()) {
            Map<String, Class<?>> stringClassMap = SpiLoader.loaderMap.get(Serializer.class.getName());
            if (stringClassMap != null && !stringClassMap.isEmpty()) {
                List<String> keys = SerializerKeys.keys();
                for (String customKey : stringClassMap.keySet()) {
                    if (!keys.contains(customKey)) {
                        Class<?> customImplClass = stringClassMap.get(customKey);
                        return SpiLoader.getInstance(Serializer.class, customImplClass.getSimpleName());
                    }
                }
            }
            // 如果没有自定义实现类，使用默认的 JDK 序列化器
            return new JdkSerializer();
        }
        // 如果 key 有值，按 key 加载配置文件中的对应实现类
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
