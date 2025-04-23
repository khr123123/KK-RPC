package org.khr;


import org.junit.jupiter.api.Test;
import org.khr.loadbalancer.LoadBalancer;
import org.khr.loadbalancer.impl.ConsistentHashLoadBalancer;
import org.khr.loadbalancer.impl.RandomLoadBalancer;
import org.khr.loadbalancer.impl.RoundRobinLoadBalancer;
import org.khr.model.ServiceMetaInfo;

import java.util.*;


/**
 * 负载均衡器测试
 */
public class LoadBalancerTest {

    final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void select() {
        // 请求参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "apple");
        List<ServiceMetaInfo> list = new ArrayList<>();
        list.add(new ServiceMetaInfo("myService", "1.0", "localhost", 1234, "default"));
        list.add(new ServiceMetaInfo("myService", "1.0", "localhost", 1235, "default"));
        list.add(new ServiceMetaInfo("myService", "1.0", "localhost", 1236, "default"));

        for (int i = 0; i < 10; i++) {
            System.out.println(loadBalancer.select(requestParams,list));
        }

    }
}
