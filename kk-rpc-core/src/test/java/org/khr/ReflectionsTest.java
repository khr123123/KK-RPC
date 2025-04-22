package org.khr;

import org.junit.Test;
import org.khr.serializer.Serializer;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author KK
 * @create 2025-04-22-16:07
 */
public class ReflectionsTest {
    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("") // 所有包
                .addClassLoaders(Thread.currentThread().getContextClassLoader()));
        Set<Class<? extends Serializer>> subTypes = reflections.getSubTypesOf(Serializer.class);
        for (Class<? extends Serializer> implClass : subTypes) {
            Serializer instance = implClass.getDeclaredConstructor().newInstance();
            System.out.println("自动发现: " + instance);
        }
    }
}
