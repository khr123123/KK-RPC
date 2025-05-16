package org.khr.kkrpcspringbootstarter.globalTX.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.khr.kkrpcspringbootstarter.globalTX.connection.KKConnection;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.KKTransactionManager;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class DataSourceAspect {

    /**
     * 切的是一个接口，所以所有的实现类都会被切到
     * spring肯定会调用这个方法来生成一个本地事务
     * 所以point.proceed()返回的也是一个Connection
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (KKTransactionManager.getCurrentTransaction() != null) {
            return new KKConnection((Connection) joinPoint.proceed(), KKTransactionManager.getCurrentTransaction());
        } else {
            return (Connection) joinPoint.proceed();
        }
    }
}
