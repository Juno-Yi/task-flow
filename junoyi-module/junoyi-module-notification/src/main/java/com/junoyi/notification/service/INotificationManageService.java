package com.junoyi.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.dto.NotificationDTO;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.vo.NotificationListVO;

/**
 * 通知管理业务接口
 *
 * @author Fan
 */
public interface INotificationManageService {

    /**
     * 获取通知列表（分页）
     * @param page 分页参数
     * @return 通知列表（分页）
     */
    PageResult<NotificationListVO> getNotificationList(Page<Notification> page);

    /**
     * 添加通知
     * @param dto 通知DTO
     */
    void addNotification(NotificationDTO dto);

    /**
     * 修改通知
     * @param dto 通知DTO
     */
    void updateNotification(NotificationDTO dto);

}
