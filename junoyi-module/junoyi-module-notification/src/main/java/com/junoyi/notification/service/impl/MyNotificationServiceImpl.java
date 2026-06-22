package com.junoyi.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.po.NotificationUserState;
import com.junoyi.notification.domain.vo.MyNotificationVO;
import com.junoyi.notification.mapper.NotificationMapper;
import com.junoyi.notification.mapper.NotificationTargetMapper;
import com.junoyi.notification.mapper.NotificationUserStateMapper;
import com.junoyi.notification.service.IMyNotificationService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
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
            vo.setContent(notification.getContent());
            vo.setType(notification.getType());
            vo.setRead(userState.getIsRead());
            vo.setReadTime(userState.getReadTime());

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
}