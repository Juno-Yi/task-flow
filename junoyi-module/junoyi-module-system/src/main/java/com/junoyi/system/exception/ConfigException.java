package com.junoyi.system.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 系统参数配置异常类
 * 用于处理系统参数配置相关的业务异常
 *
 * @author Fan
 */
public class ConfigException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建一个包含错误码、错误信息和领域标识的配置异常
     *
     * @param code    错误码
     * @param message 错误信息描述
     * @param domain  领域标识，用于区分不同业务领域的异常
     */
    public ConfigException(int code, String message, String domain) {
        super(code, message, domain);
    }

    /**
     * 构造函数，创建一个包含错误码和错误信息的配置异常
     * 领域标识默认为null
     *
     * @param code    错误码
     * @param message 错误信息描述
     */
    public ConfigException(int code, String message) {
        super(code, message, null);
    }

    /**
     * 构造函数，创建一个只包含错误信息的配置异常
     * 错误码默认为400（Bad Request），领域标识默认为null
     *
     * @param message 错误信息描述
     */
    public ConfigException(String message) {
        super(400, message, null);
    }

    /**
     * 获取领域前缀标识
     *
     * @return 返回配置异常的领域前缀"CONFIG"
     */
    @Override
    public String getDomainPrefix() {
        return "CONFIG";
    }
}
