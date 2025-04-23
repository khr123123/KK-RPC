package org.khr.orderserviceimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class OrderServiceImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceImplApplication.class, args);
    }
}
