package com.junoyi.system.exception;

/**
 * 用户手机号已存在异常
 * <p>
 * 当尝试创建或更新用户时，如果手机号已被其他用户使用，则抛出此异常
 *
 * @author Fan
 */
public class UserPhoneAlreadyExistsException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建手机号已存在异常
     *
     * @param phonenumber 重复的手机号
     */
    public UserPhoneAlreadyExistsException(String phonenumber) {
        super(400, "手机号 '" + phonenumber + "' 已被使用", "PHONE_EXISTS");
    }

    /**
     * 构造函数，创建手机号已存在异常（不带手机号）
     */
    public UserPhoneAlreadyExistsException() {
        super(400, "手机号已被使用", "PHONE_EXISTS");
    }
}
