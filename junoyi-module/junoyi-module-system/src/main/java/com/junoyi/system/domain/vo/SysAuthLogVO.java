package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志 VO 数据对象实体
 *
 * @author Fan
 */
@Data
public class SysAuthLogVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * IP归属地
     */
    private String ipRegion;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 身份（角色名称）
     */
    private String identity;

    /**
     * 登录方式（password-账号密码, wechat_work-企业微信）
     */
    private String loginType;

    /**
     * 登录方式名称
     */
    private String loginTypeName;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备类型（Desktop/Mobile/Tablet）
     */
    private String deviceType;

    /**
     * 登录状态（0-失败，1-成功）
     */
    private Integer status;

    /**
     * 提示消息（失败原因等）
     */
    private String msg;

    /**
     * 登录时间
     */
    private Date loginTime;
}