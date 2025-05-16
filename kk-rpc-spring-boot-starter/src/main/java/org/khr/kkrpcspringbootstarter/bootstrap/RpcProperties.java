package org.khr.kkrpcspringbootstarter.bootstrap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "rpc")
@Configuration
public class RpcProperties {

    private String name = "ｋｋ-ｒｐｃ";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer serverPort = 8080;
    private String serializer;
    private String loadBalancer;
    private String retryStrategy;
    private String tolerantStrategy;
    private boolean mock = false;
    private boolean needServer = true;
    private RegistryConfig registryConfig = new RegistryConfig();
    private GlobalTXConfig globalTXConfig = new GlobalTXConfig();

    @Data
    public static class RegistryConfig {
        private String registry;
        private String address = "http://localhost:2379";
        private String username;
        private String password;
        private Long timeout = 10000L;
    }

    @Data
    public static class GlobalTXConfig {
        private boolean enableGlobalTX = true;
    }
}
