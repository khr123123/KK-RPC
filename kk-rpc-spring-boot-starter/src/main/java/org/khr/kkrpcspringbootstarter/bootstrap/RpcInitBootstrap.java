package org.khr.kkrpcspringbootstarter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.khr.RpcApplication;
import org.khr.config.RpcConfig;
import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.khr.server.HttpServer;
import org.khr.server.http.HttpServerHandler;
import org.khr.server.http.VertxHttpServer;
import org.khr.server.tcp.VertxTcpServer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架启动    Spring 初始化时执行
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
//            VertxTcpServer vertxTcpServer = new VertxTcpServer();
//            vertxTcpServer.doStart(rpcConfig.getServerPort());
            HttpServer vertxTcpServer = new VertxHttpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动 server");
        }

    }
}
