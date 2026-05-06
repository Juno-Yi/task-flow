package com.junoyi.system.exception;

/**
 * 系统角色受保护异常
 * 当尝试删除系统内置角色时抛出
 *
 * @author Fan
 */
public class SystemRoleProtectedException  extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public SystemRoleProtectedException() {
        super("系统内置角色不允许删除");
    }

    public SystemRoleProtectedException(String message) {
        super(message);
    }

    public SystemRoleProtectedException(Long roleId) {
        super("角色ID " + roleId + " 是系统内置角色，不允许删除");
    }
}