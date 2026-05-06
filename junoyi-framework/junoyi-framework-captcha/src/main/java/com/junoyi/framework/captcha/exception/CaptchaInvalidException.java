package com.junoyi.framework.captcha.exception;

/**
 * 验证码无效异常类
 * 继承自CaptchaException，用于处理验证码无效的情况
 *
 * @author Fan
 */
public class CaptchaInvalidException extends CaptchaException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public CaptchaInvalidException(String message) {
        super(501, message, "INVALID");
    }
}
