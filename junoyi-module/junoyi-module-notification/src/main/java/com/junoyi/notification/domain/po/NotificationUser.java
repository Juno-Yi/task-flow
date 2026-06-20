package com.junoyi.notification.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户通知 PO 数据对象
 *
 * @author Fan
 */
@Data
@TableName("notification_user")
public class NotificationUser {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 查看读取时间
     */
    private Date readTime;

    /**
     * 通知发布时间
     */
    private Date createTime;
}