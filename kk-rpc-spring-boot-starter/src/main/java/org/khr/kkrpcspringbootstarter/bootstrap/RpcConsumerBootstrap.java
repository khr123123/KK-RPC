package org.khr.kkrpcspringbootstarter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.khr.kkrpcspringbootstarter.annotation.RpcReference;
import org.khr.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Field;

/**
 * Rpc 服务消费者启动    Spring完成Bean初始化前后，对 Bean 进行增强、替换或包装。
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor, PriorityOrdered {

    /**
     * Bean 初始化后执行，注入服务
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                log.info("注入实例对象:{}", interfaceClass);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public int getOrder() {
        return 100;  // 确保晚于 Seata
    }
}
