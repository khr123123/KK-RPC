package org.khr.orderpaymentimpl.service.impl;


import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService
public class PaymentServiceImpl implements PaymentService {


    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public boolean pay(double amount) {
        log.info("Paying " + amount + "....");
        return true;
    }
}