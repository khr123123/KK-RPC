package org.khr.fault.tolerant.impl;

import lombok.extern.slf4j.Slf4j;
import org.khr.fault.tolerant.TolerantStrategy;
import org.khr.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取降级的服务并调用
        return null;
    }
}
