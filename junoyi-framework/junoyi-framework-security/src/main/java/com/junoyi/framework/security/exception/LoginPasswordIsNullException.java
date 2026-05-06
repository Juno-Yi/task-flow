package com.junoyi.framework.security.exception;

/**
 * 登录密码为空异常类
 * 用于处理用户登录时密码为空的情况
 *
 * @author Fan
 */
public class LoginPasswordIsNullException extends LoginException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public LoginPasswordIsNullException(String message) {
        super(401, message, "PASSWORD_IS_NULL");
    }
}
