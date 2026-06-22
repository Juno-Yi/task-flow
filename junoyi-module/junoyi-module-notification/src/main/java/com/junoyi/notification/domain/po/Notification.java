package com.junoyi.notification.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 通知 PO 数据实体对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("notification")
public class Notification extends BaseEntity {

    /**
     * 通知主键ID
     */
    @TableId
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知概况
     */
    private String summary;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     * 通知状态
     */
    private Integer status;

    /**
     * 通知发送者ID
     */
    private Long senderId;

    /**
     * 发布时间
     */
    private Date publishTime;
}