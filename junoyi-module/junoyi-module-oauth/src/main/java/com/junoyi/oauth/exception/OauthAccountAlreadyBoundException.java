package com.junoyi.oauth.exception;

/**
 * OAuth账号已被绑定异常
 * 当第三方账号已被其他用户绑定时抛出此异常
 *
 * @author Fan
 */
public class OauthAccountAlreadyBoundException extends OauthException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public OauthAccountAlreadyBoundException(String message) {
        super(409, message, "ACCOUNT_ALREADY_BOUND");
    }
}

