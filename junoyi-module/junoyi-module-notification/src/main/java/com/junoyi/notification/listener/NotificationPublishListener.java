package com.junoyi.notification.listener;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.event.enums.EventPriority;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.notification.event.NotificationPublishEvent;
import lombok.RequiredArgsConstructor;

/**
 * 通知发布时间监听器
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class NotificationPublishListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(NotificationPublishListener.class);


    /**
     * 当通知发布时间触发（优先级：正常，异步：开启）
     * @param event 通知发布时间
     */
    @EventHandler(priority = EventPriority.NORMAL, async = true)
    public void onNotificationPublishEvent(NotificationPublishEvent event){

    }
}