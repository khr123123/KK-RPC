package org.khr.stockserviceimpl.service.impl;


import jakarta.annotation.Resource;
import org.khr.kkrpcspringbootstarter.annotation.RpcService;
import org.khr.service.StoreService;
import org.khr.stockserviceimpl.mapper.StockMapper;

@RpcService
public class StoreServiceImpl implements StoreService {

    @Resource
    private StockMapper stockMapper;

    @Override
    public void reduceStock(String product, int stock) {
        int i = stockMapper.reduceStock(product, stock);
    }
}