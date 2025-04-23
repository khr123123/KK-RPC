package org.khr.service;

public interface StoreService {
    boolean reduceStock(String product, int quantity);
}