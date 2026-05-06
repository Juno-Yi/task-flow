package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统登录日志 PO 数据对象实体类
 *
 * @author Fan
 */
@Data
public class SysAuthLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
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