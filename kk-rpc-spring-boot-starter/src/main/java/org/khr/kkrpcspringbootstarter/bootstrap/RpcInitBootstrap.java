package org.khr.kkrpcspringbootstarter.bootstrap;

import io.vertx.core.Vertx;
import org.khr.RpcApplication;
import org.khr.config.RpcConfig;
import org.khr.kkrpcspringbootstarter.annotation.EnableRpc;
import org.khr.kkrpcspringbootstarter.handel.HttpServerSpringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架启动    Spring 初始化时执行
 */
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    @Autowired
    private HttpServerSpringHandler httpServerSpringHandler;

    /**
     * Spring 初始化时执行，初始化 RPC 框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

//        // 启动服务器
//        if (needServer) {
////自定义的tcp协议            VertxTcpServer vertxTcpServer = new VertxTcpServer();
////            vertxTcpServer.doStart(rpcConfig.getServerPort());
////http协议          HttpServer vertxTcpServer = new VertxHttpServer();
////            vertxTcpServer.doStart(rpcConfig.getServerPort());
//            Integer port = rpcConfig.getServerPort();
//            // 创建 Vert.x 实例
//            Vertx vertx = Vertx.vertx();
//            // 创建 HTTP 服务器
//            io.vertx.core.http.HttpServer server = vertx.createHttpServer();
//            // 处理请求
//            server.requestHandler(httpServerSpringHandler);
//            // 启动 HTTP 服务器并监听指定端口
//            server.listen(port, result -> {
//                if (result.succeeded()) {
//                    System.out.println("Server is now listening on port " + port);
//                } else {
//                    System.err.println("Failed to start server: " + result.cause());
//                }
//            });
//        } else {
//            System.err.println("不启动 server");
//        }

    }
}
