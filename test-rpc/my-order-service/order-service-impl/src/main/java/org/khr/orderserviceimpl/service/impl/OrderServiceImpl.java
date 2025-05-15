package org.khr.orderserviceimpl.service.impl;


import io.seata.spring.annotation.GlobalTransactional;
import org.khr.kkrpcspringbootstarter.annotation.RpcReference;
import org.khr.orderserviceimpl.mapper.OrderMapper;
import org.khr.service.OrderService;
import org.khr.service.PaymentService;
import org.khr.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @RpcReference
    private StoreService storeService;
    @RpcReference
    private PaymentService paymentService;


    @Override
    @GlobalTransactional
    public boolean placeOrder(String product, int quantity) {
        log.info("创建订单......");
        orderMapper.insertOrder(product, quantity);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("减库存....");
        storeService.reduceStock(product, quantity);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("付款....");
        int i = 1 / 0;
        System.out.println("i = " + i);
        paymentService.pay(100.0);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        return true;
    }
}
