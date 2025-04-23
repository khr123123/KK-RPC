package org.khr;

import org.junit.jupiter.api.Test;
import org.khr.config.ConfigUtils;
import org.khr.config.RpcConfig;

/**
 * @author KK
 * @create 2025-04-22-13:24
 */
public class ConfigTest {
    @Test
    public void testLoadConfig() {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc", "dev");
        System.out.println("rpc = " + rpc);
    }

    @Test
    public void testLoadConfig2() {
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig = " + rpcConfig);
    }
}
