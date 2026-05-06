package com.junoyi.system.exception;

/**
 * 系统内置配置保护异常
 * 当尝试修改或删除系统内置配置时抛出
 *
 * @author Fan
 */
public class ConfigSystemProtectedException extends ConfigException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param message 异常信息描述
     */
    public ConfigSystemProtectedException(String message) {
        super(403, message, "SYSTEM_PROTECTED");
    }
}
