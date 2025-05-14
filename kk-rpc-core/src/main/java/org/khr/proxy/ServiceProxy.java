package org.khr.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.khr.RpcApplication;
import org.khr.config.RpcConfig;
import org.khr.constant.RpcConstant;
import org.khr.fault.retry.RetryStrategy;
import org.khr.fault.retry.RetryStrategyFactory;
import org.khr.fault.tolerant.TolerantStrategy;
import org.khr.fault.tolerant.TolerantStrategyFactory;
import org.khr.loadbalancer.LoadBalancer;
import org.khr.loadbalancer.LoadBalancerFactory;
import org.khr.model.RpcRequest;
import org.khr.model.RpcResponse;
import org.khr.model.ServiceMetaInfo;
import org.khr.registry.Registry;
import org.khr.registry.RegistryFactory;
import org.khr.serializer.Serializer;
import org.khr.serializer.SerializerFactory;
import org.khr.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Slf4j
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        log.info("NetX发送请求: {}", selectedServiceMetaInfo);
        // http 请求
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("rpc serializer: {}", serializer);
        // byte[] bodyBytes = serializer.serialize(rpcRequest);
        //RpcResponse rpcResponse = this.doHttpRequest(serviceMetaInfo1, bodyBytes);
        RpcResponse rpcResponse;
        // rpc 请求
        // 使用重试机制
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
//                    doTcpRequest(selectedServiceMetaInfo, rpcRequest);
                    {
                        byte[] bodyBytes = serializer.serialize(rpcRequest);
                        return doHttpRequest(selectedServiceMetaInfo, bodyBytes);
                    }
            );
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }

    /**
     * 发送自定义的 TCP 协议请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     */
    private static RpcResponse doTcpRequest(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) {
        try {
            return VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送 HTTP 请求
     *
     * @param selectedServiceMetaInfo
     * @param bodyBytes
     * @return
     * @throws IOException
     */
    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws IOException {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // 发送 HTTP 请求
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
