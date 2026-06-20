package com.junoyi.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.notification.domain.po.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知 Mapper
 *
 * @author Fan
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
