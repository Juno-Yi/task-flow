package com.junoyi.notification.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通知发布日志 PO 数据对象
 *
 * @author Fan
 */
@Data
@TableName("notification_publish_log")
public class NotificationPublishLog {

    @TableId
    private Long id;

    private Long notificationId;
}