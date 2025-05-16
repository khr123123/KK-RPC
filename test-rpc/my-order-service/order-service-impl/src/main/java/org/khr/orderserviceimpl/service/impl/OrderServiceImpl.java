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
    @GlobalTransactional(name = "order", rollbackFor = Exception.class)
    public boolean placeOrder(String product, int quantity) {
        log.info("创建订单......");
        orderMapper.insertOrder(product, quantity);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");


        log.info("减库存....");
        storeService.reduceStock(product, quantity);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        String a = null;
        a.toLowerCase();


        log.info("付款....");
        paymentService.pay(100.0);
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        return true;
    }
}
