package com.junoyi.framework.wework.exception;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 企业微信业务异常
 *
 * @author Fan
 */
public class WeWorkException extends BaseException {

    private static final long serialVersionUID = 1L;

    public WeWorkException(int code, String message, String domain) {
        super(code, message, domain);
    }

    public WeWorkException(int code, String message) {
        super(code, message, null);
    }

    public WeWorkException(String message) {
        super(HttpStatus.BAD_REQUEST, message, null);
    }

    @Override
    public String getDomainPrefix() {
        return "WEWORK";
    }
}

