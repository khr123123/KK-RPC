package org.khr.stockserviceimpl.service.impl;


import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RpcService
public class StoreServiceImpl implements StoreService {
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    private Map<String, Integer> stock;

    public StoreServiceImpl() {
        stock = new HashMap<>();
        // 初始化库存
        stock.put("Apple", 50);
        stock.put("Banana", 30);
    }

    @Override
    public boolean reduceStock(String product, int quantity) {
        if (stock.containsKey(product) && stock.get(product) >= quantity) {
            stock.put(product, stock.get(product) - quantity);
            log.info("成功扣减库存: " + product + " 数量: " + quantity);
            return true;
        }
        log.info("库存不足: " + product);
        return false;
    }
}