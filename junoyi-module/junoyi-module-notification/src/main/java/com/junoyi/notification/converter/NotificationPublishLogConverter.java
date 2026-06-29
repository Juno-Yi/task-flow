package com.junoyi.notification.converter;

import com.junoyi.notification.domain.po.NotificationPublishLog;
import com.junoyi.notification.domain.vo.NotificationPublishLogVO;

/**
 * 通知发布日志转换器
 *
 * @author Fan
 */
public final class NotificationPublishLogConverter {

    /**
     * 将 PO 转换成 VO
     * @param po PO数据对象
     * @return 返回VO
     */
    public static NotificationPublishLogVO toVO(NotificationPublishLog po){
        NotificationPublishLogVO notificationPublishLogVO = new NotificationPublishLogVO();
        notificationPublishLogVO.setNotificationId(po.getNotificationId());
        notificationPublishLogVO.setNotificationId(po.getNotificationId());
        notificationPublishLogVO.setPublishUserId(po.getPublishUserId());
        notificationPublishLogVO.setPublishTime(po.getPublishTime());
        return notificationPublishLogVO;
    }
}