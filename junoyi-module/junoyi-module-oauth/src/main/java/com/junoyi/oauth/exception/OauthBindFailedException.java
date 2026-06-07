package com.junoyi.oauth.exception;

/**
 * OAuth绑定失败异常
 * 用于OAuth绑定过程中的一般性错误
 *
 * @author Fan
 */
public class OauthBindFailedException extends OauthException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public OauthBindFailedException(String message) {
        super(400, message, "BIND_FAILED");
    }

    /**
     * 构造函数，支持异常链
     * @param message 异常信息
     * @param cause 原始异常
     */
    public OauthBindFailedException(String message, Throwable cause) {
        super(400, message, "BIND_FAILED");
        initCause(cause);
    }
}

