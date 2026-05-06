package com.junoyi.system.exception;

/**
 * 配置类型无效异常
 * 当配置类型不符合要求时抛出
 *
 * @author Fan
 */
public class ConfigTypeInvalidException extends ConfigException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param configType 无效的配置类型
     */
    public ConfigTypeInvalidException(String configType) {
        super(400, "不支持的配置类型: " + configType, "TYPE_INVALID");
    }
}
