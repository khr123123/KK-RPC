package org.khr.kkrpcspringbootstarter.handel;

import io.vertx.core.Vertx;
import org.khr.RpcApplication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VertxServerStarter implements InitializingBean {

    @Autowired
    private HttpServerSpringHandler httpServerSpringHandler;

    @Override
    public void afterPropertiesSet() {
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
