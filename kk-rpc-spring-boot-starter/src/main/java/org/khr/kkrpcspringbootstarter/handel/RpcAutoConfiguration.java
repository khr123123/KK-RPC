package org.khr.kkrpcspringbootstarter.handel;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.khr.kkrpcspringbootstarter.handel") // 你的 BeanUtil 包路径
public class RpcAutoConfiguration {
}
