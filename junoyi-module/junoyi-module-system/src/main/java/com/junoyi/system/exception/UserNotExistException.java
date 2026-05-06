package com.junoyi.system.exception;

/**
 * 用户不存在异常类
 * 用于表示用户不存在的业务异常情况
 *
 * @author Fan
 */
public class UserNotExistException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UserNotExistException(String message) {
        super(404, message, "NOT_EXIST");
    }
}
