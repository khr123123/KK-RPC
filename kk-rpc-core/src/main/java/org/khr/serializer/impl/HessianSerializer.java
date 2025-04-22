package org.khr.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.khr.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 * 🧩 特点：
 * 🚀 高性能：采用二进制格式，体积小，适合网络传输。
 * 🔄 跨语言支持：虽然主要为 Java 设计，但也支持其他语言（如 PHP、Python）。
 * 💡 使用简单：使用 Java 原生的 InputStream/OutputStream 接口进行序列化和反序列化。
 */
public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(bos);
        ho.writeObject(object);
        ho.flush();
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(bis);
        return (T) hi.readObject(tClass);
    }
}
