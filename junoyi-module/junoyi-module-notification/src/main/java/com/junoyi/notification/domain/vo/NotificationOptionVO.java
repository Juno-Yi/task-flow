package com.junoyi.notification.domain.vo;

import lombok.Data;

/**
 * 通知下拉列表 VO
 *
 * @author Fan
 */
@Data
public class NotificationOptionVO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;
}