package com.junoyi.project.exception;

/**
 * 项目不存在异常类
 *
 * @author Fan
 */
public class ProjectNotFoundException extends ProjectException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public ProjectNotFoundException(String message) {
        super(404, message, "NOT_FOUND");
    }
}
