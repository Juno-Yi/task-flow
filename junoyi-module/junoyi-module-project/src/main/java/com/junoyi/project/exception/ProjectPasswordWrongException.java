package com.junoyi.project.exception;

/**
 * 项目删除密码错误异常类
 *
 * @author Fan
 */
public class ProjectPasswordWrongException extends ProjectException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public ProjectPasswordWrongException(String message) {
        super(400, message, "PASSWORD_WRONG");
    }
}
