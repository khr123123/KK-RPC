package org.khr.orderserviceimpl.controller;

import org.khr.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author KK
 * @create 2025-04-23-16:17
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    //http://localhost:7003/order?product=Apple&quantity=10
    @GetMapping("/order")
    public boolean orderService(String product, int quantity) {
        return orderService.placeOrder(product, quantity);
    }
}
