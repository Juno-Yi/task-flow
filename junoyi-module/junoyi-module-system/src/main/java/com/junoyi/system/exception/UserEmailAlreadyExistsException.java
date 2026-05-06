package com.junoyi.system.exception;

/**
 * 用户邮箱已存在异常
 * <p>
 * 当尝试创建或更新用户时，如果邮箱已被其他用户使用，则抛出此异常
 *
 * @author Fan
 */
public class UserEmailAlreadyExistsException extends UserException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建邮箱已存在异常
     *
     * @param email 重复的邮箱
     */
    public UserEmailAlreadyExistsException(String email) {
        super(400, "邮箱 '" + email + "' 已被使用", "EMAIL_EXISTS");
    }

    /**
     * 构造函数，创建邮箱已存在异常（不带邮箱）
     */
    public UserEmailAlreadyExistsException() {
        super(400, "邮箱已被使用", "EMAIL_EXISTS");
    }
}
