package com.junoyi.system.exception;

/**
 * 用户状态被禁用异常类
 * 当用户状态为禁用状态时抛出此异常
 *
 * @author Fan
 */
public class UserStatusIsDisableException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UserStatusIsDisableException(String message) {
        super(403, message, "STATUS_IS_DISABLE");
    }
}
