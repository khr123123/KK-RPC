package org.khr.kkrpcspringbootstarter.handel;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.khr.RpcApplication;
import org.khr.kkrpcspringbootstarter.bootstrap.RpcProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component  // InitializingBean -----> Spring 完成 Bean 的依赖注入之后
public class VertxServerStarter implements InitializingBean {

    @Autowired
    private HttpServerSpringHandler httpServerSpringHandler;
    @Autowired
    private RpcProperties rpcProperties;

    @Override
    public void afterPropertiesSet() {
        boolean needServer = rpcProperties.isNeedServer();
        if (!needServer) {
            log.info("手动关闭了 KK-RPC服务..");
            return;
        }
        // 这里保证所有依赖都注入完成，可以安全使用 httpServerSpringHandler
        Integer port = RpcApplication.getRpcConfig().getServerPort();
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        server.requestHandler(httpServerSpringHandler);
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + res.cause());
            }
        });
    }
}
