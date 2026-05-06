package com.junoyi.system.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 部门异常类，继承自BaseException
 * 用于处理部门相关的异常情况
 *
 * @author Fan
 */
public class DeptException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数，创建带有错误码、消息和域的部门异常
     * @param code 错误码
     * @param message 异常消息
     * @param domain 异常域
     */
    public DeptException(int code, String message, String domain) {
        super(code, message, domain);
    }

    /**
     * 构造函数，创建带有错误码和消息的部门异常
     * @param code 错误码
     * @param message 异常消息
     */
    public DeptException(int code, String message) {
        super(code, message, null);
    }

    /**
     * 构造函数，创建带有默认错误码的消息的部门异常
     * @param message 异常消息
     */
    public DeptException(String message) {
        super(501, message, null);
    }

    /**
     * 获取域前缀
     * @return 返回"DEPT"作为域前缀
     */
    @Override
    public String getDomainPrefix() {
        return "DEPT";
    }
}
