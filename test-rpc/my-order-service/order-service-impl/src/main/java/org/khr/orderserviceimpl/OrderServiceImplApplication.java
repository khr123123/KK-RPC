package org.khr.orderserviceimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
@MapperScan("org.khr.orderserviceimpl.mapper")
public class OrderServiceImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceImplApplication.class, args);
    }

}
