package org.khr.kkrpcspringbootstarter.globalTX.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 开启分布式事务注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalTransactional {

    /**
     * 该方法调用的远程服务数量（用于监控或校验）
     */
    int remoteServiceCount() default 0;
}
