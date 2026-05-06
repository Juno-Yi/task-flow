package com.junoyi.framework.permission.exception;

import com.junoyi.framework.core.constant.HttpStatus;

/**
 * 无权限异常
 *
 * @author Fan
 */
public class NoPermissionException extends PermissionException {

    private static final long serialVersionUID = 1L;

    private static final String DOMAIN = "NO_PERMISSION";

    /**
     * 缺少的权限节点
     */
    private final String[] requiredPermissions;

    public NoPermissionException() {
        super(HttpStatus.FORBIDDEN, "没有访问权限", DOMAIN);
        this.requiredPermissions = new String[0];
    }

    public NoPermissionException(String message) {
        super(HttpStatus.FORBIDDEN, message, DOMAIN);
        this.requiredPermissions = new String[0];
    }

    public NoPermissionException(String... requiredPermissions) {
        super(HttpStatus.FORBIDDEN, "没有访问权限", DOMAIN);
        this.requiredPermissions = requiredPermissions != null ? requiredPermissions : new String[0];
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }
}
