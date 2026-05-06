package com.junoyi.framework.security.exception;

/**
 * 登录账户为空异常类
 * 该异常用于处理登录时账户信息为空的情况，继承自LoginException
 *
 * @author Fan
 */
public class LoginAccountIsNullException extends LoginException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建一个账户为空的登录异常
     * @param message 异常消息描述
     */
    public LoginAccountIsNullException(String message) {
        super(401, message, "ACCOUNT_IS_NULL");
    }
}
