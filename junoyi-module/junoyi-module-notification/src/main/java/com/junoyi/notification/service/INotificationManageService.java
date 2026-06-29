package com.junoyi.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.dto.NotificationDTO;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.vo.NotificationDetailVO;
import com.junoyi.notification.domain.vo.NotificationListVO;
import com.junoyi.notification.domain.vo.NotificationOptionVO;

import java.util.List;

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
     * 获取通知下拉列表
     * @return 通知下拉列表
     */
    List<NotificationOptionVO> getNotificationOptions();

    /**
     * 获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    NotificationDetailVO getNotificationById(Long id);

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

    /**
     * 发布通知（将草稿状态改为已发布）
     * @param notificationId 通知ID
     */
    void publishNotification(Long notificationId);

}
