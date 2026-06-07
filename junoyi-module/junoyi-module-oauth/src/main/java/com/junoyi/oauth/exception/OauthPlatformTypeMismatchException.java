package com.junoyi.oauth.exception;

/**
 * OAuth平台类型不匹配异常
 * 当绑定时平台类型与预期不符时抛出此异常
 *
 * @author Fan
 */
public class OauthPlatformTypeMismatchException extends OauthException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public OauthPlatformTypeMismatchException(String message) {
        super(400, message, "PLATFORM_TYPE_MISMATCH");
    }
}

