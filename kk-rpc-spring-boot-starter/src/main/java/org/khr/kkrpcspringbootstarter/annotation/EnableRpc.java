package org.khr.kkrpcspringbootstarter.annotation;

import org.khr.kkrpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import org.khr.kkrpcspringbootstarter.bootstrap.RpcInitBootstrap;
import org.khr.kkrpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Rpc 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     */
    boolean needServer() default true;
}
