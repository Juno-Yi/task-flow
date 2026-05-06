package com.junoyi.system.event;

import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.event.domain.BaseEvent;
import com.junoyi.framework.security.utils.SecurityUtils;
import lombok.Getter;

/**
 * 用户操作事件
 *
 * @author Fan
 */
@Getter
public class UserOperationEvent extends BaseEvent {

    /**
     * 日志级别：info/warn/error
     */
    private final String level;

    /**
     * 动作：view/create/update/delete/export/import/execute
     */
    private final String action;

    /**
     * 模块：vulnerability/webshell/project/user等
     */
    private final String module;

    /**
     * 操作用户ID
     */
    private final Long userId;

    /**
     * 用户名
     */
    private final String userName;

    /**
     * 昵称
     */
    private final String nickName;

    /**
     * 详情描述
     */
    private final String message;

    /**
     * 对象ID
     */
    private final String targetId;

    /**
     * 对象名称
     */
    private final String targetName;

    /**
     * 请求路径
     */
    private final String path;

    /**
     * 请求方法
     */
    private final String method;

    /**
     * 操作IP
     */
    private final String ip;

    /**
     * 原始数据JSON
     */
    private final String rawData;

    /**
     * 完整构造函数
     */
    public UserOperationEvent(String level, String action, String module,
                             Long userId, String userName, String nickName,
                             String message, String targetId, String targetName,
                             String path, String method, String ip, String rawData) {
        super();
        this.level = level;
        this.action = action;
        this.module = module;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.message = message;
        this.targetId = targetId;
        this.targetName = targetName;
        this.path = path;
        this.method = method;
        this.ip = ip;
        this.rawData = rawData;
    }

    /**
     * 简化构造函数（info级别）
     */
    public UserOperationEvent(String action, String module, String message,
                             String targetId, String targetName,
                             Long userId, String userName, String nickName,
                             String ip) {
        this("info", action, module, userId, userName, nickName,
                message, targetId, targetName, null, null, ip, null);
    }

    /**
     * 最简构造函数（用于业务模块，自动获取用户信息）
     */
    public static UserOperationEvent of(String action, String module, String message,
                                       String targetId, String targetName) {
        return createEvent("info", action, module, message, targetId, targetName, null);
    }

    /**
     * 带rawData的构造函数
     */
    public static UserOperationEvent withRawData(String action, String module, String message,
                                                String targetId, String targetName, String rawData) {
        return createEvent("info", action, module, message, targetId, targetName, rawData);
    }

    /**
     * 指定级别的构造函数
     */
    public static UserOperationEvent withLevel(String level, String action, String module, String message,
                                              String targetId, String targetName) {
        return createEvent(level, action, module, message, targetId, targetName, null);
    }

    /**
     * 创建事件（在当前线程获取用户和请求信息）
     */
    private static UserOperationEvent createEvent(String level, String action, String module,
                                                 String message, String targetId, String targetName,
                                                 String rawData) {
        Long userId = null;
        String userName = null;
        String nickName = null;
        String ip = null;
        String path = null;
        String method = null;

        // 获取当前用户信息
        try {
            userId = SecurityUtils.getUserId();
            userName = SecurityUtils.getUserName();
            nickName = SecurityUtils.getNickName();
        } catch (Exception ignored) {
            // 未登录情况下忽略
        }

        // 获取请求信息
        try {
            ip = ServletUtils.getClientIp();
            if (ServletUtils.getRequest() != null) {
                path = ServletUtils.getRequest().getRequestURI();
                method = ServletUtils.getRequest().getMethod();
            }
        } catch (Exception ignored) {
            // 非HTTP请求上下文忽略
        }

        return new UserOperationEvent(level, action, module,
                userId, userName, nickName,
                message, targetId, targetName,
                path, method, ip, rawData);
    }
}