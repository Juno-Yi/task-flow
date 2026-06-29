package com.junoyi.notification.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 通知发布日志查询 DTO
 *
 * @author Fan
 */
@Data
public class NotificationPublishLogQueryDTO {

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 发布用户ID
     */
    private Long publishUserId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
