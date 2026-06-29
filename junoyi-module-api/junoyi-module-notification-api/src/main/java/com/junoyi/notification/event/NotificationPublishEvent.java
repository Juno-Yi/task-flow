package com.junoyi.notification.event;

import com.junoyi.framework.event.domain.BaseEvent;
import lombok.Getter;

/**
 * 消息通知发布事件
 * <li>
 * 当消息通知发布时候，将触发该事件
 * </li>
 *
 * @author Fan
 */
@Getter
public class NotificationPublishEvent extends BaseEvent {

    /**
     * 通知ID
     */
    private final Long notificationId;

    /**
     * 发布通知的用户ID
     */
    private final Long publishUserId;

    /**
     * 构造方法
     * @param notificationId 通知ID
     * @param publishUserId 发布通知的用户ID
     */
    public NotificationPublishEvent(Long notificationId, Long publishUserId){
        this.notificationId = notificationId;
        this.publishUserId = publishUserId;
    }

}