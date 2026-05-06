package com.junoyi.framework.security.exception;

/**
 * Token 过期异常
 * 用于 RefreshToken 过期或被撤销的场景
 *
 * @author Fan
 */
public class TokenExpiredException extends AuthException {

    private static final long serialVersionUID = 1L;

    private static final String DOMAIN = "TOKEN_EXPIRED";

    public TokenExpiredException() {
        super(401, "登录已过期，请重新登录", DOMAIN);
    }

    public TokenExpiredException(String message) {
        super(401, message, DOMAIN);
    }
}
