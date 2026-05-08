package com.junoyi.oauth.exception;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.core.domain.base.BaseException;

/**
 * OAuth业务异常
 *
 * @author Fan
 */
public class OauthException extends BaseException {

    private static final long serialVersionUID = 1L;

    public OauthException(int code, String message, String domain) {
        super(code, message, domain);
    }

    public OauthException(int code, String message) {
        super(code, message, null);
    }

    public OauthException(String message) {
        super(HttpStatus.BAD_REQUEST, message, null);
    }

    @Override
    public String getDomainPrefix() {
        return "OAUTH";
    }
}

