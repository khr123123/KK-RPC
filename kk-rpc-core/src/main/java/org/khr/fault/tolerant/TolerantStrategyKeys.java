package org.khr.fault.tolerant;

/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {

    /**
     * 故障恢复
     */
    String FAIL_BACK = "FailBackTolerantStrategy";

    /**
     * 快速失败
     */
    String FAIL_FAST = "FailFastTolerantStrategy";

    /**
     * 故障转移
     */
    String FAIL_OVER = "FailOverTolerantStrategy";

    /**
     * 静默处理
     */
    String FAIL_SAFE = "FailSafeTolerantStrategy";

}
