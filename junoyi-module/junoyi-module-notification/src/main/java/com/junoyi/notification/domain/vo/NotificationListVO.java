package com.junoyi.notification.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 通知 VO 数据对象
 *
 * @author Fan
 */
@Data
public class NotificationListVO {

    /**
     * 通知主键ID
     */
    private Long id;

    /**
     * 通知标签
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
     *  通知类型标签（字典翻译）
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

    private String statusLabel;

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
     * 发布时间
     */
    public Date publishTime;

    /**
     * 更新时间
     */
    public Date updateTime;
}