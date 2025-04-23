package org.khr.orderserviceimpl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.khr.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author KK
 * @create 2025-04-23-16:38
 */
@SpringBootTest
public class orderserviceimpl {
    @Autowired
    private OrderService orderService;
    @Test
    public void placeOrder() {
        boolean b = orderService.placeOrder("Apple", 10);
        System.out.println("b = " + b);
    }
}
