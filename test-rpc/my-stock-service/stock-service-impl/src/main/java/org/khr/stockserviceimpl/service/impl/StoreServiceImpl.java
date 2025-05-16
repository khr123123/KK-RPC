package org.khr.stockserviceimpl.service.impl;


import jakarta.annotation.Resource;
import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.service.StoreService;
import org.khr.stockserviceimpl.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@RpcService
public class StoreServiceImpl implements StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    @Resource
    private StockMapper stockMapper;

    @Override
    @Transactional
    public void reduceStock(String product, int stock) {
        log.info("扣减库存。。。");
        int i = stockMapper.reduceStock(product, stock);
    }
}