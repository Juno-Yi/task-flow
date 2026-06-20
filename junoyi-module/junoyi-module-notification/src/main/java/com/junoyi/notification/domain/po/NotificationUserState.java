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
public class NotificationUserState {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 查看读取时间
     */
    private Date readTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 通知发布时间
     */
    private Date createTime;
}