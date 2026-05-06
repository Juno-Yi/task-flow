package com.junoyi.system.exception;

/**
 * 配置值无效异常
 * 当配置值不符合类型要求时抛出
 *
 * @author Fan
 */
public class ConfigValueInvalidException extends ConfigException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param message 异常信息描述
     */
    public ConfigValueInvalidException(String message) {
        super(400, message, "VALUE_INVALID");
    }
}
