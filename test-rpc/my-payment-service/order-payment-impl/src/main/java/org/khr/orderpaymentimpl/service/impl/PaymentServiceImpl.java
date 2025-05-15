package org.khr.orderpaymentimpl.service.impl;


import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.orderpaymentimpl.mapper.BalanceMapper;
import org.khr.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RpcService
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private BalanceMapper balanceMapper;

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public boolean pay(double amount) {
        int i = balanceMapper.reduceBalance(123L, amount);
        log.info(" Paying " + amount + "....");
        return i > 0;
    }

}