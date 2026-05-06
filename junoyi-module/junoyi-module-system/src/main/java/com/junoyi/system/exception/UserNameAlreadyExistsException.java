package com.junoyi.system.exception;

/**
 * 用户名已存在异常
 * <p>
 * 当尝试创建或更新用户时，如果用户名已被其他用户使用，则抛出此异常
 *
 * @author Fan
 */
public class UserNameAlreadyExistsException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建用户名已存在异常
     *
     * @param userName 重复的用户名
     */
    public UserNameAlreadyExistsException(String userName) {
        super(400, "用户名 '" + userName + "' 已存在", "USERNAME_EXISTS");
    }

    /**
     * 构造函数，创建用户名已存在异常（不带用户名）
     */
    public UserNameAlreadyExistsException() {
        super(400, "用户名已存在", "USERNAME_EXISTS");
    }
}
