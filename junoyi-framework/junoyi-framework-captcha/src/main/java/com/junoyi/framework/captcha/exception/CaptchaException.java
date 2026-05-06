package com.junoyi.framework.captcha.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 验证码异常类，继承自BaseException
 * 用于处理验证码相关的异常情况
 *
 * @author Fan
 */
public class CaptchaException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建验证码异常实例
     * @param code 异常代码
     * @param message 异常消息
     * @param domain 异常域
     */
    public CaptchaException(int code, String message, String domain) {
        super(code, message, domain);
    }

    /**
     * 构造函数，创建验证码异常实例（无域信息）
     * @param code 异常代码
     * @param message 异常消息
     */
    public CaptchaException(int code, String message) {
        super(code, message, null);
    }

    /**
     * 构造函数，创建验证码异常实例（默认异常代码501，无域信息）
     * @param message 异常消息
     */
    public CaptchaException(String message) {
        super(501, message, null);
    }

    /**
     * 获取验证码异常的域前缀
     * @return 返回"CAPTCHA"作为验证码异常的域前缀
     */
    @Override
    public String getDomainPrefix() {
        return "CAPTCHA";
    }
}
