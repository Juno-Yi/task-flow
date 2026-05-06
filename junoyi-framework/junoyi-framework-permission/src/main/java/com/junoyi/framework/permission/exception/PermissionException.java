package com.junoyi.framework.permission.exception;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 权限异常基类
 * <p>
 * Permission 领域的所有异常都应继承此类
 *
 * @author Fan
 */
public abstract class PermissionException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 领域前缀
     */
    private static final String DOMAIN_PREFIX = "PERMISSION";


    /**
     * 构造函数，创建带有错误码、消息和域信息的PermissionException实例
     * @param code 错误码
     * @param message 异常消息
     * @param domain 错误域
     */
    public PermissionException(int code, String message, String domain) {
        super(code, message, domain);
    }

    /**
     * 构造函数，创建带有错误码和消息的PermissionException实例，域信息默认为null
     * @param code 错误码
     * @param message 异常消息
     */
    public PermissionException(int code, String message){
        super(code, message, null);
    }

    /**
     * 构造函数，创建带有默认错误码403和消息的PermissionException实例
     * @param message 异常消息
     */
    public PermissionException(String message){
        super(403,message,null);
    }

    /**
     * 获取域前缀
     * @return 返回域前缀常量
     */
    @Override
    public String getDomainPrefix() {
        return DOMAIN_PREFIX;
    }

}
