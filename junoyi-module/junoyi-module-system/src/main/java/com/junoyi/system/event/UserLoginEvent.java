package com.junoyi.system.event;

import com.junoyi.framework.event.domain.BaseEvent;
import com.junoyi.framework.security.module.LoginUser;
import lombok.Getter;


/**
 * 用户登录事件
 * 当用户进行登录时候触发该事件
 *
 * @author Fan
 */
@Getter
public class UserLoginEvent extends BaseEvent {
    /**
     * 用户ID（登录失败时可能为空）
     */
    private final Long userId;

    /**
     * 用户名
     */
    private final String userName;

    /**
     * 用户昵称
     */
    private final String nickName;

    /**
     * 登录IP
     */
    private final String loginIp;

    /**
     * 会话ID（登录成功时有值）
     */
    private final String sessionId;

    /**
     * 身份（角色名称）
     */
    private final String identity;

    /**
     * 登录方式
     */
    private final String loginType;

    /**
     * User-Agent
     */
    private final String userAgent;

    /**
     * 登录状态（true-成功，false-失败）
     */
    private final boolean success;

    /**
     * 失败消息
     */
    private final String failMessage;

    /**
     * 登录成功事件构造
     */
    public UserLoginEvent(LoginUser loginUser, String loginIp, String sessionId, String loginType, String userAgent) {
        super();
        this.userId = loginUser.getUserId();
        this.userName = loginUser.getUserName();
        this.nickName = loginUser.getNickName();
        this.loginIp = loginIp;
        this.sessionId = sessionId;
        this.identity = loginUser.isSuperAdmin() ? "超级管理员" : "普通用户";
        this.loginType = loginType;
        this.userAgent = userAgent;
        this.success = true;
        this.failMessage = null;
    }

    /**
     * 登录失败事件构造
     */
    public UserLoginEvent(String userName, String loginIp, String loginType, String userAgent, String failMessage) {
        super();
        this.userId = null;
        this.userName = userName;
        this.nickName = null;
        this.loginIp = loginIp;
        this.sessionId = null;
        this.identity = null;
        this.loginType = loginType;
        this.userAgent = userAgent;
        this.success = false;
        this.failMessage = failMessage;
    }
}