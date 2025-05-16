package org.khr.kkrpcspringbootstarter.globalTX.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.khr.kkrpcspringbootstarter.globalTX.annotation.GlobalTransactional;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.KKTransactionManager;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.Transaction;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.TransactionType;
import org.khr.server.HttpHeadContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class TransactionAspect implements Ordered {


    @Around("@annotation(org.khr.kkrpcspringbootstarter.globalTX.annotation.GlobalTransactional)")
    public Object invoke(ProceedingJoinPoint point) {
        // 打印出这个注解所对应的方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        GlobalTransactional Annotation = method.getAnnotation(GlobalTransactional.class);
        //...可拓展
        int remoteServiceCount = Annotation.remoteServiceCount();
        String groupId = KKTransactionManager.createTransactionGroup(remoteServiceCount);
        Transaction transaction = KKTransactionManager.createTransaction(groupId, true);
        log.info("开启全局的事务,方法名{},事务组ID{},主方法事务id{}", method.getName(), groupId, transaction.getTransactionId());
        // 存入上下文中
        HttpHeadContext.set("groupId", groupId);
        Object result;
        try {
            // spring会开启mysql事务
            result = point.proceed();
            KKTransactionManager.addTransaction(transaction, TransactionType.commit);
        } catch (Throwable throwable) {
            KKTransactionManager.addTransaction(transaction, TransactionType.rollback);
            throw new RuntimeException("入口方法出错了...");
        }
        return result;
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
