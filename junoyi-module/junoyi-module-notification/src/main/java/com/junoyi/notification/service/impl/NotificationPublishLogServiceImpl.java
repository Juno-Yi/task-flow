package com.junoyi.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.converter.NotificationPublishLogConverter;
import com.junoyi.notification.domain.dto.NotificationPublishLogQueryDTO;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.po.NotificationPublishLog;
import com.junoyi.notification.domain.vo.NotificationPublishLogVO;
import com.junoyi.notification.mapper.NotificationMapper;
import com.junoyi.notification.mapper.NotificationPublishLogMapper;
import com.junoyi.notification.service.INotificationPublishLogService;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知发布日志业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class NotificationPublishLogServiceImpl implements INotificationPublishLogService {

    private final NotificationPublishLogMapper notificationPublishLogMapper;
    private final NotificationMapper notificationMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取列表（分页）
     * @param queryDTO 查询参数
     * @param page 分页参数
     * @return 返回列表
     */
    @Override
    public PageResult<NotificationPublishLogVO> getList(NotificationPublishLogQueryDTO queryDTO, Page<NotificationPublishLog> page) {
        // 构建查询条件
        LambdaQueryWrapper<NotificationPublishLog> queryWrapper = new LambdaQueryWrapper<>();
        // 按通知ID查询
        if (queryDTO.getNotificationId() != null) {
            queryWrapper.eq(NotificationPublishLog::getNotificationId, queryDTO.getNotificationId());
        }
        // 按发布用户ID查询
        if (queryDTO.getPublishUserId() != null) {
            queryWrapper.eq(NotificationPublishLog::getPublishUserId, queryDTO.getPublishUserId());
        }
        // 按时间范围查询
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(NotificationPublishLog::getPublishTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(NotificationPublishLog::getPublishTime, queryDTO.getEndTime());
        }
        // 按发布时间降序排列（最新的在前面）
        queryWrapper.orderByDesc(NotificationPublishLog::getPublishTime);

        // 分页查询
        Page<NotificationPublishLog> result = notificationPublishLogMapper.selectPage(page, queryWrapper);

        if (result.getRecords().isEmpty()) {
            return PageResult.of(new ArrayList<>(), result.getTotal(), (int) page.getCurrent(), (int) page.getSize());
        }

        // 获取关联的通知信息
        List<Long> notificationIds = result.getRecords().stream()
                .map(NotificationPublishLog::getNotificationId)
                .distinct()
                .collect(Collectors.toList());

        List<Notification> notifications = notificationMapper.selectBatchIds(notificationIds);
        Map<Long, Notification> notificationMap = notifications.stream()
                .collect(Collectors.toMap(Notification::getId, n -> n));

        // 获取关联的用户信息
        List<Long> userIds = result.getRecords().stream()
                .map(NotificationPublishLog::getPublishUserId)
                .filter(userId -> userId != null && userId > 0)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SysUser> userMap = userIds.isEmpty() ?
            Map.of() :
            sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getUserId, userIds)
                    .select(SysUser::getUserId, SysUser::getNickName, SysUser::getUserName)
            ).stream().collect(Collectors.toMap(SysUser::getUserId, u -> u));

        // PO 转换 VO
        List<NotificationPublishLogVO> voList = result.getRecords().stream()
                .map(log -> {
                    NotificationPublishLogVO vo = NotificationPublishLogConverter.toVO(log);

                    // 设置通知标题和摘要
                    Notification notification = notificationMap.get(log.getNotificationId());
                    if (notification != null) {
                        vo.setNotificationTitle(notification.getTitle());
                        vo.setNotificationSummary(notification.getSummary());
                    }

                    // 设置发布者昵称
                    Long publishUserId = log.getPublishUserId();
                    if (publishUserId == null || publishUserId == 0) {
                        vo.setPublishUserNickName("系统");
                    } else {
                        SysUser user = userMap.get(publishUserId);
                        vo.setPublishUserNickName(user != null ? user.getNickName() : "未知");
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
}