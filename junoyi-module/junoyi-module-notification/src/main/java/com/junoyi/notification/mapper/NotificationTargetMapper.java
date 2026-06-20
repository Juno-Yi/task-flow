package com.junoyi.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.notification.domain.po.NotificationTarget;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知目标 Mapper
 *
 * @author Fan
 */
@Mapper
public interface NotificationTargetMapper extends BaseMapper<NotificationTarget> {
}

