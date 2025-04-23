package org.khr;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/**
 * 基于 jetcd 的 etcd 客户端操作示例
 * 功能包括：put、get、delete
 *
 * @author KK
 * @create 2025-04-23-09:56
 */
public class EtcdTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        // 创建 etcd 客户端，连接到本地 etcd 实例（默认端口 2379）

        try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {
            // 获取 KV（键值）客户端
            KV kvClient = client.getKVClient();

            // 创建键值对（需要使用 ByteSequence）
            ByteSequence key = ByteSequence.from("2222".getBytes());
            ByteSequence value = ByteSequence.from("2222".getBytes());

            // 写入键值对到 etcd
            kvClient.put(key, value).get();
            System.out.println("Put 成功：key = test_key, value = test_value");

            // 创建键值对（需要使用 ByteSequence）
            ByteSequence key1 = ByteSequence.from("1111".getBytes());
            ByteSequence value1 = ByteSequence.from("11111".getBytes());

            // 写入键值对到 etcd
            kvClient.put(key1, value1).get();
            System.out.println("Put 成功：key = test_key, value = test_value");


        }
        // 关闭 etcd 客户端资源
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException {
        // 创建 etcd 客户端，连接到本地 etcd 实例（默认端口 2379）

        try (Client client = Client.builder().endpoints("http://localhost:2379").build()) {
            // 获取 KV（键值）客户端
            KV kvClient = client.getKVClient();
            // 删除该键
            ByteSequence key = ByteSequence.from("2222".getBytes());
            kvClient.delete(key).get();
            System.out.println("Delete 成功：key = test_key");
        }
        // 关闭 etcd 客户端资源
    }
}
