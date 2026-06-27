package com.junoyi.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.notification.domain.po.NotificationPublishLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知发布日志 Mapper
 *
 * @author Fan
 */
@Mapper
public interface NotificationPublishLogMapper extends BaseMapper<NotificationPublishLog> {
}
