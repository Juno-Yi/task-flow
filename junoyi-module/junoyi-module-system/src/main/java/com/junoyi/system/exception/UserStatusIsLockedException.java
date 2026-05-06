package com.junoyi.system.exception;

/**
 * 用户状态被锁定异常类
 * 当用户账户状态为锁定状态时抛出此异常
 *
 * @author Fan
 */
public class UserStatusIsLockedException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 错误消息
     */
    public UserStatusIsLockedException(String message) {
        super(403, message, "STATUS_IS_LOCKED");
    }
}
