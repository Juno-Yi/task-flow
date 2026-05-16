package com.junoyi.project.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 项目异常类
 *
 * @author Fan
 */
public class ProjectException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息描述
     * @param domain 异常当前领域
     */
    public ProjectException(int code, String message, String domain) {
        super(code, message, domain);
    }

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息描述
     */
    public ProjectException(int code, String message) {
        super(code, message, null);
    }

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public ProjectException(String message) {
        super(400, message, null);
    }

    /**
     * 获取域前缀
     * @return 返回项目领域的前缀字符串"PROJECT"
     */
    @Override
    public String getDomainPrefix() {
        return "PROJECT";
    }
}
