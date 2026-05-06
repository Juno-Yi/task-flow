package com.junoyi.framework.captcha.exception;

/**
 * 验证码不支持类型异常类
 * 继承自CaptchaException，用于处理验证码类型不支持的异常情况
 *
 * @author Fan
 */
public class CaptchaUnsupportedTypeException extends CaptchaException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常描述信息
     */
    public CaptchaUnsupportedTypeException(String message) {
        super(501, message, "UNSUPPORTED_TYPE");
    }
}
