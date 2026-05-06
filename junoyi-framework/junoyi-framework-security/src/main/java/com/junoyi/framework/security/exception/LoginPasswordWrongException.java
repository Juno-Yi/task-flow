package com.junoyi.framework.security.exception;

/**
 * 登录密码错误异常类
 * 用于处理用户登录时密码输入错误的情况
 *
 * @author Fan
 */
public class LoginPasswordWrongException extends LoginException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public LoginPasswordWrongException(String message) {
        super(401, message, "PASSWORD_WRONG");
    }
}
