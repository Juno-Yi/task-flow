package com.junoyi.notification.converter;

import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.vo.NotificationListVO;

/**
 * 通知对象转换器
 *
 * @author Fan
 */
public final class NotificationConverter {

    private NotificationConverter() {
    }

    /**
     * Notification PO 转 NotificationListVO
     *
     * @param notification PO 对象
     * @return VO 对象
     */
    public static NotificationListVO toListVO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationListVO vo = new NotificationListVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setSummary(notification.getSummary());
        vo.setContent(notification.getContent());
        vo.setType(notification.getType());
        vo.setStatus(notification.getStatus());
        vo.setSenderId(notification.getSenderId());
        vo.setPublishTime(notification.getPublishTime());
        vo.setUpdateTime(notification.getUpdateTime());

        return vo;
    }
}

