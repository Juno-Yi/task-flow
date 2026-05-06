package com.junoyi.framework.captcha.exception;

/**
 * 验证码过期异常类
 * 继承自CaptchaException，用于处理验证码已过期的异常情况
 *
 * @author Fan
 */
public class CaptchaExpiredException extends CaptchaException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常描述信息
     */
    public CaptchaExpiredException(String message) {
        super(501, message, "EXPIRED");
    }
}
