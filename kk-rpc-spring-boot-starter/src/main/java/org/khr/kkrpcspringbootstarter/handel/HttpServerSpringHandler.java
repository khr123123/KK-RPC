package org.khr.kkrpcspringbootstarter.handel;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.khr.RpcApplication;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.KKTransactionManager;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.Transaction;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.TransactionType;
import org.khr.model.RpcRequest;
import org.khr.model.RpcResponse;
import org.khr.registry.impl.LocalRegistry;
import org.khr.serializer.Serializer;
import org.khr.serializer.SerializerFactory;
import org.khr.server.HttpHeadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理器，用于处理客户端发送的 RPC 请求
 */
@Component
public class HttpServerSpringHandler implements Handler<HttpServerRequest> {

    private static final Logger log = LoggerFactory.getLogger(HttpServerSpringHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void handle(HttpServerRequest request) {
        log.info("【KKRPC】收到请求: {} {}", request.method(), request.uri());

        // 事务上下文初始化
        String groupId = request.getHeader("groupId");
        Transaction transaction = null;

        if (!StringUtil.isNullOrEmpty(groupId)) {
            KKTransactionManager.setCurrentGroupId(groupId);
            transaction = KKTransactionManager.createTransaction(groupId, false);
            // 存入上下文，后续参与者获取
            HttpHeadContext.set("groupId", groupId);
            log.info("开启全局的事务,事务组ID{},分支事务id{}", groupId, transaction.getTransactionId());
        }

        // 获取序列化方式
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        Transaction finalTransaction = transaction;

        // 处理请求体
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
                log.info("【KKRPC】反序列化请求成功: {}", rpcRequest);
            } catch (Exception e) {
                log.error("【KKRPC】反序列化请求失败", e);
            }

            RpcResponse rpcResponse = new RpcResponse();

            // 请求对象为空，直接响应错误
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                try {
                    doResponse(request, rpcResponse, serializer);
                } catch (IOException e) {
                    log.error("【KKRPC】响应发送失败", e);
                }
                return;
            }

            try {
                // 获取服务实现类
                LocalRegistry.log();
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());

                // 从 Spring 容器中获取 Bean 实例
                Object bean = applicationContext.getBean(implClass);

                // 反射调用方法
                Object result = method.invoke(bean, rpcRequest.getArgs());

                // 设置响应内容
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

                // 提交事务
                KKTransactionManager.addTransaction(finalTransaction, TransactionType.commit);
                log.info("【KKRPC】调用成功，事务提交: {}", rpcResponse.getData());

            } catch (Exception e) {
                // 回滚事务
                KKTransactionManager.addTransaction(finalTransaction, TransactionType.rollback);
                rpcResponse.setMessage("Error during method invocation: " + e.getMessage());
                rpcResponse.setException(e);
                log.error("【KKRPC】调用失败，事务回滚", e);
            }

            // 返回响应
            try {
                doResponse(request, rpcResponse, serializer);
            } catch (IOException e) {
                log.error("【KKRPC】响应发送失败", e);
            }
        });
    }

    /**
     * 响应客户端请求，发送 RpcResponse 对象
     *
     * @param request      原始 HTTP 请求
     * @param rpcResponse  要响应的数据
     * @param serializer   使用的序列化方式
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) throws IOException {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");

        try {
            // 序列化响应数据并返回
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
            log.info("【KKRPC】响应已发送: {}", rpcResponse);
        } catch (IOException e) {
            log.error("【KKRPC】响应序列化失败", e);
            httpServerResponse.end(Buffer.buffer());
            throw e;
        }
    }
}
