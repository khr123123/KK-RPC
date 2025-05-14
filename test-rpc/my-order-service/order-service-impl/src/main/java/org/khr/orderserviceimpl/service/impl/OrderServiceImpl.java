package org.khr.orderserviceimpl.service.impl;


import org.khr.kkrpcspringbootstarter.annotation.RpcReference;
import org.khr.service.OrderService;
import org.khr.service.PaymentService;
import org.khr.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @RpcReference
    private StoreService storeService;
    @RpcReference
    private PaymentService paymentService;


    @Override
    public boolean placeOrder(String product, int quantity) {
        log.info("创建订单成功");
        // 调用库存服务扣减库存
        storeService.reduceStock(product, quantity);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        paymentService.pay(100.0);
        return true;
    }
}
