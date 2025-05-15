package org.khr.orderpaymentimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
@MapperScan("org.khr.orderpaymentimpl.mapper")
public class OrderPaymentImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPaymentImplApplication.class, args);
    }

}
