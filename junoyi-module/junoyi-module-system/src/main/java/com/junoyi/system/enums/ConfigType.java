package com.junoyi.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统参数类型枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum ConfigType {

    /**
     * 文本类型
     */
    TEXT("text", "文本"),

    /**
     * 数字类型
     */
    NUMBER("number", "数字"),

    /**
     * 布尔类型
     */
    BOOLEAN("boolean", "布尔"),

    /**
     * JSON对象类型
     */
    JSON("json", "JSON对象");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 配置类型枚举，如果不存在则返回null
     */
    public static ConfigType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ConfigType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 验证代码是否有效
     *
     * @param code 类型代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
