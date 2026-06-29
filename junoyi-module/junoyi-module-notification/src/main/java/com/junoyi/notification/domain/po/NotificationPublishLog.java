package com.junoyi.notification.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 通知发布日志 PO 数据对象
 *
 * @author Fan
 */
@Data
@TableName("notification_publish_log")
public class NotificationPublishLog {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 发布人ID
     */
    private Long publishUserId;

    /**
     * 发布时间
     */
    private Date publishTime;

}