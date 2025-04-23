package org.khr.config;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 加载配置对象（默认环境）
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持环境区分
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        String baseFileName = "application" + (StrUtil.isNotBlank(environment) ? "-" + environment : "");
        String propertiesPath = baseFileName + ".properties";
        String yamlPath = baseFileName + ".yml";

        try {
            // 1. 优先尝试读取 .properties
            Props props = new Props(propertiesPath);
            return props.toBean(tClass, prefix);
        } catch (NoResourceException ignored) {
            // 2. fallback 到 .yml
            Dict dict = YamlUtil.loadByPath(yamlPath);
            LinkedHashMap<Object, Object> rawMap = dict.getBean(prefix);
            if (rawMap == null) {
                throw new IllegalArgumentException("未找到配置前缀：" + prefix);
            }
            try {
                T configInstance = tClass.getDeclaredConstructor().newInstance();
                for (Object keyObj : rawMap.keySet()) {
                    String key = keyObj.toString();
                    Object value = rawMap.get(key);
                    try {
                        Field field = tClass.getDeclaredField(key);
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();

                        if (isCustomClass(fieldType)) {
                            // 递归转换嵌套对象
                            if (value instanceof Map) {
                                Object nested = parseConfig((Map<?, ?>) value, fieldType); // 递归方法
                                field.set(configInstance, nested);
                            }
                        } else {
                            // 普通字段直接转换
                            Object converted = convertValue(value, fieldType);
                            field.set(configInstance, converted);
                        }

                    } catch (NoSuchFieldException | IllegalAccessException ignoredField) {
                        // log ignoredField if needed
                    }
                }
                return configInstance;
            } catch (Exception e) {
                throw new RuntimeException("配置绑定失败：" + e.getMessage(), e);
            }
        }
    }

    /**
     * 判断是否是自定义的对象类型
     *
     * @param clazz
     * @return
     */
    private static boolean isCustomClass(Class<?> clazz) {
        return !(clazz.isPrimitive() || clazz.getName().startsWith("java."));
    }

    private static <T> T parseConfig(Map<?, ?> rawMap, Class<T> tClass) throws Exception {
        T configInstance = tClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            try {
                Field field = tClass.getDeclaredField(key);
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                if (isCustomClass(fieldType)) {
                    if (value instanceof Map) {
                        // 嵌套 map 结构，递归解析为对象
                        Object nested = parseConfig((Map<?, ?>) value, fieldType);
                        field.set(configInstance, nested);
                    } else {
                        throw new IllegalArgumentException("嵌套配置字段解析失败，字段名：" + key + "，值类型：" + value.getClass());
                    }
                } else {
                    Object converted = convertValue(value, fieldType);
                    field.set(configInstance, converted);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // 可选 log ignored
            }
        }

        return configInstance;
    }

    /**
     * 类型转换工具（支持基础类型、包装类、enum、String）
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;
        String str = value.toString();
        if (targetType == String.class) return str;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(str);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(str);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(str);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(str);
        if (targetType == float.class || targetType == Float.class) return Float.parseFloat(str);
        if (targetType == short.class || targetType == Short.class) return Short.parseShort(str);
        if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(str);
        if (targetType.isEnum()) return Enum.valueOf((Class<Enum>) targetType, str);
        return value;
    }
}
