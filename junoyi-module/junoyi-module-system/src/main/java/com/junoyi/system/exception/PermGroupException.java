package com.junoyi.system.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 权限组异常基类
 *
 * @author Fan
 */
public class PermGroupException extends BaseException {

    private static final long serialVersionUID = 1L;

    public PermGroupException(int code, String message, String domain) {
        super(code, message, domain);
    }

    public PermGroupException(String message) {
        super(501, message, null);
    }

    @Override
    public String getDomainPrefix() {
        return "PERM_GROUP";
    }
}
