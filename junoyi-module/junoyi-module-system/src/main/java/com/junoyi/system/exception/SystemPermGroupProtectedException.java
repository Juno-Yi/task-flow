package com.junoyi.system.exception;


/**
 * 系统权限组受保护异常
 * 当尝试删除系统内置权限组时抛出
 *
 * @author Fan
 */
public class SystemPermGroupProtectedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SystemPermGroupProtectedException() {
        super("系统内置权限组不允许删除");
    }

    public SystemPermGroupProtectedException(String message) {
        super(message);
    }

    public SystemPermGroupProtectedException(Long groupId) {
        super("权限组ID " + groupId + " 是系统内置权限组，不允许删除");
    }
}