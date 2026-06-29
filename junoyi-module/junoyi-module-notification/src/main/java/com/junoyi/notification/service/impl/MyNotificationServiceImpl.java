package com.junoyi.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.po.NotificationUserState;
import com.junoyi.notification.domain.vo.MyNotificationDetailVO;
import com.junoyi.notification.domain.vo.MyNotificationVO;
import com.junoyi.notification.mapper.NotificationMapper;
import com.junoyi.notification.mapper.NotificationTargetMapper;
import com.junoyi.notification.mapper.NotificationUserStateMapper;
import com.junoyi.notification.service.IMyNotificationService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 我的通知业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class MyNotificationServiceImpl implements IMyNotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationTargetMapper notificationTargetMapper;
    private final NotificationUserStateMapper notificationUserStateMapper;
    private final SysDictApi sysDictApi;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取我的通知列表（分页）
     * @param userId 用户Id
     * @param page 分页
     * @return 我的通知列表（分页）
     */
    @Override
    public PageResult<MyNotificationVO> getMyNotificationList(Long userId, Page<Notification> page) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 分页查询当前用户的通知状态记录（已发布、未删除）
        Page<NotificationUserState> userStatePage = new Page<>(page.getCurrent(), page.getSize());
        IPage<NotificationUserState> userStateResult = notificationUserStateMapper.selectPage(
                userStatePage,
                new LambdaQueryWrapper<NotificationUserState>()
                        .eq(NotificationUserState::getUserId, userId)
                        .eq(NotificationUserState::getIsDelete, false)
                        .orderByDesc(NotificationUserState::getCreateTime)
        );

        List<NotificationUserState> userStates = userStateResult.getRecords();
        if (userStates.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 提取通知ID列表
        List<Long> notificationIds = userStates.stream()
                .map(NotificationUserState::getNotificationId)
                .collect(Collectors.toList());

        // 批量查询通知详情
        List<Notification> notifications = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .in(Notification::getId, notificationIds)
                        .eq(Notification::getStatus, 1) // 只查询已发布的
        );

        if (notifications.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 构建通知ID到通知对象的映射
        Map<Long, Notification> notificationMap = notifications.stream()
                .collect(Collectors.toMap(Notification::getId, n -> n, (v1, v2) -> v1));

        // 获取字典数据
        List<SysDictDataVO> typeDict = sysDictApi.getDictDataByType("notification_type");
        Map<String, SysDictDataVO> typeDictMap = typeDict.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, d -> d, (v1, v2) -> v1));

        // 获取所有发布者ID
        Set<Long> senderIds = notifications.stream()
                .map(Notification::getSenderId)
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());

        // 批量查询发布者信息
        Map<Long, SysUser> senderMap = new HashMap<>();
        if (!senderIds.isEmpty()) {
            List<SysUser> senders = sysUserMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .in(SysUser::getUserId, senderIds)
                            .select(SysUser::getUserId, SysUser::getNickName)
            );
            senderMap = senders.stream()
                    .collect(Collectors.toMap(SysUser::getUserId, s -> s, (v1, v2) -> v1));
        }

        // 组装VO
        List<MyNotificationVO> voList = new ArrayList<>();
        for (NotificationUserState userState : userStates) {
            Notification notification = notificationMap.get(userState.getNotificationId());
            if (notification == null) {
                continue; // 通知不存在或未发布，跳过
            }

            MyNotificationVO vo = new MyNotificationVO();
            vo.setId(notification.getId());
            vo.setTitle(notification.getTitle());
            vo.setSummary(notification.getSummary());
            // 不设置 content，列表不需要内容详情
            vo.setType(notification.getType());
            vo.setRead(userState.getIsRead());
            vo.setReadTime(userState.getReadTime());

            // 设置发布者信息
            Long senderId = notification.getSenderId();
            if (senderId == null || senderId == 0) {
                vo.setPublishedBy("系统");
            } else {
                SysUser sender = senderMap.get(senderId);
                vo.setPublishedBy(sender != null ? sender.getNickName() : "系统");
            }

            // 设置发布时间
            vo.setPublishedAt(notification.getPublishTime());

            // 字典翻译
            SysDictDataVO dictData = typeDictMap.get(String.valueOf(notification.getType()));
            if (dictData != null) {
                vo.setTypeLabel(dictData.getDictLabel());
                vo.setTypeType(dictData.getListClass());
            }

            voList.add(vo);
        }

        return PageResult.of(voList, userStateResult.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 获取我的通知详情（同时标记为已读）
     */
    @Override
    public MyNotificationDetailVO getMyNotificationDetail(Long userId, Long notificationId) {
        // 查询通知基本信息
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("通知不存在");
        }

        // 查询用户阅读状态
        NotificationUserState userState = notificationUserStateMapper.selectOne(
                new LambdaQueryWrapper<NotificationUserState>()
                        .eq(NotificationUserState::getUserId, userId)
                        .eq(NotificationUserState::getNotificationId, notificationId)
        );

        if (userState == null) {
            throw new IllegalArgumentException("无权查看该通知");
        }

        // 标记为已读（如果未读）
        if (!userState.getIsRead()) {
            userState.setIsRead(true);
            userState.setReadTime(new Date());
            notificationUserStateMapper.updateById(userState);
        }

        // 组装详情 VO
        MyNotificationDetailVO vo = new MyNotificationDetailVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setSummary(notification.getSummary());
        vo.setContent(notification.getContent());
        vo.setType(notification.getType());
        vo.setRead(userState.getIsRead());
        vo.setReadTime(userState.getReadTime());

        // 设置发布者信息
        Long senderId = notification.getSenderId();
        if (senderId == null || senderId == 0) {
            vo.setPublishedBy("系统");
        } else {
            SysUser sender = sysUserMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUserId, senderId)
                            .select(SysUser::getUserId, SysUser::getNickName)
            );
            vo.setPublishedBy(sender != null ? sender.getNickName() : "系统");
        }

        // 设置发布时间
        vo.setPublishedAt(notification.getPublishTime());

        // 字典翻译
        List<SysDictDataVO> typeDict = sysDictApi.getDictDataByType("notification_type");
        SysDictDataVO dictData = typeDict.stream()
                .filter(d -> d.getDictValue().equals(String.valueOf(notification.getType())))
                .findFirst()
                .orElse(null);

        if (dictData != null) {
            vo.setTypeLabel(dictData.getDictLabel());
            vo.setTypeType(dictData.getListClass());
        }

        return vo;
    }

    /**
     * 全部标记为已读
     */
    @Override
    public int markAllAsRead(Long userId) {
        // 查询当前用户所有未读的通知状态
        List<NotificationUserState> unreadStates = notificationUserStateMapper.selectList(
                new LambdaQueryWrapper<NotificationUserState>()
                        .eq(NotificationUserState::getUserId, userId)
                        .eq(NotificationUserState::getIsRead, false)
        );

        if (unreadStates.isEmpty()) {
            return 0;
        }

        // 批量更新为已读
        Date now = new Date();
        unreadStates.forEach(state -> {
            state.setIsRead(true);
            state.setReadTime(now);
        });

        // 批量更新数据库
        unreadStates.forEach(state -> notificationUserStateMapper.updateById(state));

        return unreadStates.size();
    }

    /**
     * 获取用户未读数量
     */
    @Override
    public Long getUnreadCount(Long userId) {
        return notificationUserStateMapper.selectCount(
                new LambdaQueryWrapper<NotificationUserState>()
                        .eq(NotificationUserState::getUserId, userId)
                        .eq(NotificationUserState::getIsRead, false)
        );
    }
}