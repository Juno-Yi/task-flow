package com.junoyi.notification.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通知目标 PO 数据对象
 *
 * @author Fan
 */
@Data
@TableName("notification_target")
public class NotificationTarget {

    /**
     *  主键ID
     */
    @TableId
    private Long id;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 通知目标类型
     */
    private Integer targetType;

    /**
     * 通知目标ID
     */
    private Long targetId;
}