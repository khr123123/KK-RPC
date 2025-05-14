package org.khr.orderpaymentimpl;

import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class OrderPaymentImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPaymentImplApplication.class, args);
    }

}
