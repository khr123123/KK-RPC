package org.khr.orderpaymentimpl.service.impl;


import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.orderpaymentimpl.mapper.BalanceMapper;
import org.khr.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@RpcService
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private BalanceMapper balanceMapper;

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    @Transactional
    public boolean pay(double amount) {
        int i = balanceMapper.reduceBalance(123L, amount);
        log.info(" Paying " + amount + "....");
        String a = null;
        a.toLowerCase();
        return i > 0;
    }

}