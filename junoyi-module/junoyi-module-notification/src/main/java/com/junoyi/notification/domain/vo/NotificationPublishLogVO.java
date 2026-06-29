package com.junoyi.notification.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 通知发布日志 VO
 *
 * @author Fan
 */
@Data
public class NotificationPublishLogVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 通知标题
     */
    private String notificationTitle;

    /**
     * 通知概况
     */
    private String notificationSummary;

    /**
     * 发布人ID
     */
    private Long publishUserId;

    /**
     * 发布者昵称
     */
    private String publishUserNickName;

    /**
     * 发布时间
     */
    private Date publishTime;

}