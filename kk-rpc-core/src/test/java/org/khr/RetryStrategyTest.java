package org.khr;

import org.junit.jupiter.api.Test;
import org.khr.fault.retry.RetryStrategy;
import org.khr.fault.retry.impl.FixedIntervalRetryStrategy;
import org.khr.fault.retry.impl.NoRetryStrategy;
import org.khr.model.RpcResponse;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
