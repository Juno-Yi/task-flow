package com.junoyi.system.exception;

/**
 * 配置键名已存在异常
 * 当尝试添加已存在的配置键名时抛出
 *
 * @author Fan
 */
public class ConfigKeyExistsException extends ConfigException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param configKey 已存在的配置键名
     */
    public ConfigKeyExistsException(String configKey) {
        super(400, "配置键名已存在: " + configKey, "KEY_EXISTS");
    }
}
