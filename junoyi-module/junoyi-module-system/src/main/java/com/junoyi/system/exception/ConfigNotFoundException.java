package com.junoyi.system.exception;

/**
 * 配置不存在异常
 * 当查询的配置不存在时抛出
 *
 * @author Fan
 */
public class ConfigNotFoundException extends ConfigException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param message 异常信息描述
     */
    public ConfigNotFoundException(String message) {
        super(404, message, "NOT_FOUND");
    }

    /**
     * 构造函数（默认消息）
     */
    public ConfigNotFoundException() {
        super(404, "配置不存在", "NOT_FOUND");
    }
}
