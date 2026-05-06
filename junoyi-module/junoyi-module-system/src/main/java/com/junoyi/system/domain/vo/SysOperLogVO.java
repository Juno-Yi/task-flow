package com.junoyi.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统操作日志 VO 数据实体对象
 *
 * @author Fan
 */
@Data
public class SysOperLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 日志级别标签
     */
    private String levelLabel;

    /**
     * 动作
     */
    private String action;

    /**
     * 动作标签
     */
    private String actionLabel;

    /**
     * 模块
     */
    private String module;

    /**
     * 模块标签
     */
    private String moduleLabel;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 详情描述
     */
    private String message;

    /**
     * 对象ID
     */
    private String targetId;

    /**
     * 对象名称
     */
    private String targetName;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 原始数据JSON
     */
    private String rawData;

    /**
     * 操作时间
     */
    private Date createTime;
}