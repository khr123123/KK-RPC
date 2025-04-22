package org.khr;

import org.junit.Test;
import org.khr.serializer.Serializer;

import java.util.ServiceLoader;

/**
 * @author KK
 * @create 2025-04-22-16:02
 */
public class JDKSPITest {
    @Test
    public void test() {
        ServiceLoader<Serializer> load = ServiceLoader.load(Serializer.class);
        for (Serializer serializer : load) {
            System.out.println("serializer = " + serializer);
        }
    }
}
