package org.khr.fault.tolerant.impl;

import lombok.extern.slf4j.Slf4j;
import org.khr.fault.tolerant.TolerantStrategy;
import org.khr.model.RpcResponse;

import java.util.Map;

/**
 * 静默处理异常 - 容错策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常", e);
        return new RpcResponse();
    }
}
