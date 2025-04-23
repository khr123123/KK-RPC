package org.khr.stockserviceimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class StockServiceImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockServiceImplApplication.class, args);
    }
}
