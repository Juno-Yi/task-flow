package com.junoyi.system.domain.po;

import com.junoyi.framework.security.enums.PlatformType;
import lombok.Data;

import java.util.Date;

/**
 * 系统会话数据实体
 * 用于会话管理接口返回的会话信息
 *
 * @author Fan
 */
@Data
public class SysSession {

    /**
     * 会话ID，唯一标识一个用户会话
     */
    private String sessionId;

    /**
     * 用户ID，标识会话所属的用户
     */
    private Long userId;

    /**
     * 用户名，用户的登录名称
     */
    private String userName;

    /**
     * 昵称，用户显示的名称
     */
    private String nickName;

    /**
     * 平台类型，标识用户登录的平台
     */
    private PlatformType platformType;

    /**
     * 登录IP地址，记录用户登录时的IP
     */
    private String loginIp;

    /**
     * IP所在地区，记录登录IP的地理位置信息
     */
    private String ipRegion;

    /**
     * 登录时间，记录用户本次会话的登录时间
     */
    private Date loginTime;

    /**
     * 最后访问时间，记录用户最后一次访问的时间
     */
    private Date lastAccessTime;

    /**
     * 用户代理（浏览器信息）
     */
    private String userAgent;

    /**
     * 设备类型（Mobile/Tablet/Desktop/Unknown）
     */
    private String deviceType;

    /**
     * 设备类型标签
     */
    private String deviceTypeLabel;

    /**
     * 操作系统（如 Windows 10、macOS、Android 13）
     */
    private String os;

    /**
     * 浏览器（如 Chrome 120、Firefox 121）
     */
    private String browser;

    /**
     * AccessToken 过期时间
     */
    private Long accessExpireTime;

    /**
     * RefreshToken 过期时间
     */
    private Long refreshExpireTime;

}
