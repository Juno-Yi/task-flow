package com.junoyi.notification.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.dto.NotificationPublishLogQueryDTO;
import com.junoyi.notification.domain.po.NotificationPublishLog;
import com.junoyi.notification.domain.vo.NotificationPublishLogVO;

/**
 * 通知发布日志业务接口
 *
 * @author Fan
 */
public interface INotificationPublishLogService {

    /**
     * 获取列表（分页）
     * @param queryDTO 查询参数
     * @param page 分页参数
     * @return 返回列表
     */
    PageResult<NotificationPublishLogVO> getList(NotificationPublishLogQueryDTO queryDTO, Page<NotificationPublishLog> page);
}
