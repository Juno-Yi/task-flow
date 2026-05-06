package com.junoyi.system.exception;

/**
 * 菜单包含子项异常类
 * 当菜单存在子项时抛出此异常，继承自MenuException
 *
 * @author Fan
 */
public class MenuHasChildrenException extends MenuException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常描述信息
     */
    public MenuHasChildrenException(String message) {
        super(501, message, "HAS_CHILDREN");
    }
}
