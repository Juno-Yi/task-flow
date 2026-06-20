package com.junoyi.notification.domain.dto;

import lombok.Data;

/**
 * 消息通知 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class NotificationDTO {

    /**
     * 主键ID(修改时候使用）
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
     * 状态
     */
    private Integer status;


}