package org.khr.kkrpcspringbootstarter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.khr.kkrpcspringbootstarter.handel,org.khr.kkrpcspringbootstarter.globalTX") // 你的 BeanUtil 包路径
public class RpcAutoConfiguration {

}
