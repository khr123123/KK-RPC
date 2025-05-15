package org.khr.orderpaymentimpl;

import org.khr.orderpaymentimpl.mapper.BalanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author KK
 * @create 2025-05-15-11:24
 */
@SpringBootTest
public class Test {

    @Autowired
    private BalanceMapper balanceMapper;

    @org.junit.jupiter.api.Test
    public void test() {
        int i = balanceMapper.reduceBalance(123L, 123);
        System.out.println("i = " + i);
    }

}
