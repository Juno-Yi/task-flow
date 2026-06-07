package com.junoyi.oauth.exception;

/**
 * OAuth绑定令牌失效异常
 * 当绑定令牌过期或不存在时抛出此异常
 *
 * @author Fan
 */
public class OauthBindTokenExpiredException extends OauthException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public OauthBindTokenExpiredException(String message) {
        super(400, message, "BIND_TOKEN_EXPIRED");
    }
}

