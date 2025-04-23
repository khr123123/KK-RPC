package org.khr.loadbalancer;

/**
 * 负载均衡器键名常量
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "RoundRobinLoadBalancer";

    String RANDOM = "RandomLoadBalancer";

    String CONSISTENT_HASH = "ConsistentHashLoadBalancer";

}
