package org.khr.orderpaymentimpl.mapper;

public interface BalanceMapper {
    int reduceBalance(Long userId, double amount);
}
