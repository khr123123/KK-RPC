package org.khr.serializer;

import java.util.List;

/**
 * 序列化器键名常量
 */
public interface SerializerKeys {
    String JDK = "JdkSerializer";
    String JSON = "JsonSerializer";
    String KRYO = "KryoSerializer";
    String HESSIAN = "HessianSerializer";

    static List<String> keys() {
        return List.of(JDK, JSON, KRYO, HESSIAN);
    }
}
