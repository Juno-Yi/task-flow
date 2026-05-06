package com.junoyi.framework.permission.exception;

import com.junoyi.framework.core.constant.HttpStatus;

/**
 * 未登录异常
 *
 * @author Fan
 */
public class NotLoginException extends PermissionException {

    private static final long serialVersionUID = 1L;

    private static final String DOMAIN = "NOT_LOGIN";

    public NotLoginException() {
        super(HttpStatus.UNAUTHORIZED, "请先登录", DOMAIN);
    }

    public NotLoginException(String message) {
        super(HttpStatus.UNAUTHORIZED, message, DOMAIN);
    }
}
