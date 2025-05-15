package org.khr.stockserviceimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
@MapperScan("org.khr.stockserviceimpl.mapper")
public class StockServiceImplApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockServiceImplApplication.class, args);
    }
}
