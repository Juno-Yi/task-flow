package com.junoyi.notification.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 通知详情 VO 数据对象
 *
 * @author Fan
 */
@Data
public class NotificationDetailVO {

    /**
     * 通知主键ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     * 通知类型标签（字典翻译）
     */
    private String typeLabel;

    /**
     * 通知类型的tip标签类型（字典翻译）
     */
    private String typeType;

    /**
     * 通知状态
     */
    private Integer status;

    /**
     * 状态标签
     */
    private String statusLabel;

    /**
     * 状态类型
     */
    private String statusType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者昵称
     */
    private String senderNickName;

    /**
     * 目标范围类型（0-全部 1-部门 2-角色 3-指定用户）
     */
    private Integer targetType;

    /**
     * 目标ID列表
     */
    private List<Long> targetIds;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

