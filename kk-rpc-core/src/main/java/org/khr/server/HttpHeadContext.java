package org.khr.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求头参数的上下文对象  ThreadLocal
 */
public class HttpHeadContext {
    private static final ThreadLocal<Map<String, String>> context = ThreadLocal.withInitial(HashMap::new);

    // 设置上下文内容
    public static void set(String key, String value) {
        context.get().put(key, value);
    }

    // 获取上下文内容
    public static String get(String key) {
        return context.get().get(key);
    }

    // 获取全部上下文
    public static Map<String, String> getAll() {
        return context.get();
    }

    // 清理上下文
    public static void clear() {
        context.remove();
    }
}
