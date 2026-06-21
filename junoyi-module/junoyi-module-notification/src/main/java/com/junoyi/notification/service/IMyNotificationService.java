package com.junoyi.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.vo.MyNotificationVO;

/**
 * 我的通知业务接口
 *
 * @author Fan
 */
public interface IMyNotificationService {

    /**
     * 获取我的通知列表（分页）
     * @param userId 用户ID
     * @param page 分页
     * @return 我的通知列表（分页）
     */
    PageResult<MyNotificationVO> getMyNotificationList(Long userId, Page<Notification> page);
}
