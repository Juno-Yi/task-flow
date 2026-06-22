package com.junoyi.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.notification.converter.NotificationConverter;
import com.junoyi.notification.domain.dto.NotificationDTO;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.po.NotificationTarget;
import com.junoyi.notification.domain.po.NotificationUserState;
import com.junoyi.notification.domain.vo.NotificationDetailVO;
import com.junoyi.notification.domain.vo.NotificationListVO;
import com.junoyi.notification.mapper.NotificationMapper;
import com.junoyi.notification.mapper.NotificationTargetMapper;
import com.junoyi.notification.mapper.NotificationUserStateMapper;
import com.junoyi.notification.service.INotificationManageService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserDept;
import com.junoyi.system.domain.po.SysUserRole;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserDeptMapper;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知管理业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class NotificationManageServiceImpl implements INotificationManageService {

    private final NotificationMapper notificationMapper;
    private final NotificationTargetMapper notificationTargetMapper;
    private final NotificationUserStateMapper notificationUserStateMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysDictApi sysDictApi;

    /**
     * 获取通知列表（分页）
     * @param page 分页参数
     * @return 通知列表（分页）
     */
    @Override
    public PageResult<NotificationListVO> getNotificationList(Page<Notification> page) {
        // 分页查询通知列表，按发布时间倒序
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .orderByDesc(Notification::getPublishTime);

        IPage<Notification> resultPage = notificationMapper.selectPage(page, wrapper);
        List<Notification> records = resultPage.getRecords();

        if (records.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 批量查询发送者用户信息
        List<Long> senderIds = records.stream()
                .map(Notification::getSenderId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SysUser> senderMap = senderIds.isEmpty()
                ? Map.of()
                : sysUserMapper.selectBatchIds(senderIds).stream()
                        .collect(Collectors.toMap(SysUser::getUserId, u -> u, (v1, v2) -> v1));

        // 批量获取字典数据
        Map<String, SysDictDataVO> typeMap = buildDictMap("notification_type");
        Map<String, SysDictDataVO> statusMap = buildDictMap("notification_status");

        // 转换为 VO 并填充字典标签
        List<NotificationListVO> voList = records.stream().map(notification -> {
            NotificationListVO vo = NotificationConverter.toListVO(notification);

            // 填充发送者昵称
            if (notification.getSenderId() != null) {
                SysUser sender = senderMap.get(notification.getSenderId());
                if (sender != null) {
                    vo.setSenderNickName(sender.getNickName());
                }
            }

            // 填充通知类型标签
            if (notification.getType() != null) {
                SysDictDataVO typeDict = typeMap.get(String.valueOf(notification.getType()));
                if (typeDict != null) {
                    vo.setTypeLabel(typeDict.getDictLabel());
                    vo.setTypeType(typeDict.getListClass());
                }
            }

            // 填充通知状态标签
            if (notification.getStatus() != null) {
                SysDictDataVO statusDict = statusMap.get(String.valueOf(notification.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, resultPage.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 构建字典映射表
     *
     * @param dictType 字典类型
     * @return 字典值为key，字典数据为value的Map
     */
    private Map<String, SysDictDataVO> buildDictMap(String dictType) {
        List<SysDictDataVO> dictList = sysDictApi.getDictDataByType(dictType);
        return dictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict, (v1, v2) -> v1));
    }

    /**
     * 获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    @Override
    public NotificationDetailVO getNotificationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("通知ID不能为空");
        }

        // 查询通知基本信息
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new IllegalArgumentException("通知不存在");
        }

        // 组装详情VO
        NotificationDetailVO detailVO = new NotificationDetailVO();
        detailVO.setId(notification.getId());
        detailVO.setTitle(notification.getTitle());
        detailVO.setSummary(notification.getSummary());
        detailVO.setContent(notification.getContent());
        detailVO.setType(notification.getType());
        detailVO.setStatus(notification.getStatus());
        detailVO.setSenderId(notification.getSenderId());
        detailVO.setPublishTime(notification.getPublishTime());
        detailVO.setUpdateTime(notification.getUpdateTime());

        // 查询字典翻译
        List<SysDictDataVO> typeDict = sysDictApi.getDictDataByType("notification_type");
        typeDict.stream()
                .filter(d -> d.getDictValue().equals(String.valueOf(notification.getType())))
                .findFirst()
                .ifPresent(d -> {
                    detailVO.setTypeLabel(d.getDictLabel());
                    detailVO.setTypeType(d.getListClass());
                });

        List<SysDictDataVO> statusDict = sysDictApi.getDictDataByType("notification_status");
        statusDict.stream()
                .filter(d -> d.getDictValue().equals(String.valueOf(notification.getStatus())))
                .findFirst()
                .ifPresent(d -> {
                    detailVO.setStatusLabel(d.getDictLabel());
                    detailVO.setStatusType(d.getListClass());
                });

        // 查询发送者昵称
        if (notification.getSenderId() != null) {
            SysUser sender = sysUserMapper.selectById(notification.getSenderId());
            if (sender != null) {
                detailVO.setSenderNickName(sender.getNickName());
            }
        }

        // 查询通知目标信息
        List<NotificationTarget> targets = notificationTargetMapper.selectList(
                new LambdaQueryWrapper<NotificationTarget>()
                        .eq(NotificationTarget::getNotificationId, id)
        );

        if (!targets.isEmpty()) {
            NotificationTarget firstTarget = targets.get(0);
            detailVO.setTargetType(firstTarget.getTargetType());

            // 如果不是全部用户，收集目标ID列表
            if (!Integer.valueOf(0).equals(firstTarget.getTargetType())) {
                List<Long> targetIds = targets.stream()
                        .map(NotificationTarget::getTargetId)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());
                detailVO.setTargetIds(targetIds);
            }
        }

        return detailVO;
    }

    /**
     * 添加通知（立即发布或存储草稿）
     * status: 0-草稿  1-已发布
     * targetType: 0-全部 1-部门 2-角色 3-指定用户 4-项目组
     * @param dto 通知DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNotification(NotificationDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("通知数据不能为空");
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException("通知类型不能为空");
        }
        if (dto.getTargetType() == null) {
            throw new IllegalArgumentException("通知目标范围不能为空");
        }
        // 非全部时，目标ID列表不能为空
        if (!Integer.valueOf(0).equals(dto.getTargetType())
                && (dto.getTargetIds() == null || dto.getTargetIds().isEmpty())) {
            throw new IllegalArgumentException("请选择通知目标");
        }

        // 插入通知主表
        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setSummary(dto.getSummary());
        notification.setContent(dto.getContent());
        notification.setType(dto.getType());
        notification.setSenderId(SecurityUtils.getUserId());
        notification.setCreateBy(SecurityUtils.getUserName());
        notification.setCreateTime(new Date());

        // 根据状态判断：1-立即发布，0-存草稿
        if (Integer.valueOf(1).equals(dto.getStatus())) {
            notification.setStatus(1);
            notification.setPublishTime(new Date());
        } else {
            notification.setStatus(0);
        }

        notificationMapper.insert(notification);

        // 插入通知目标表
        if (Integer.valueOf(0).equals(dto.getTargetType())) {
            // 全部用户：插入一条 targetType=0, targetId=null 的记录
            NotificationTarget target = new NotificationTarget();
            target.setNotificationId(notification.getId());
            target.setTargetType(0);
            notificationTargetMapper.insert(target);
        } else {
            // 部门/角色/指定用户：每个目标ID插一条记录
            for (Long targetId : dto.getTargetIds()) {
                NotificationTarget target = new NotificationTarget();
                target.setNotificationId(notification.getId());
                target.setTargetType(dto.getTargetType());
                target.setTargetId(targetId);
                notificationTargetMapper.insert(target);
            }
        }

        // 如果是立即发布，解析目标用户并批量插入用户通知状态
        if (Integer.valueOf(1).equals(dto.getStatus())) {
            List<Long> userIds = resolveTargetUserIds(dto.getTargetType(), dto.getTargetIds());
            Date now = new Date();
            for (Long userId : userIds) {
                NotificationUserState userState = new NotificationUserState();
                userState.setNotificationId(notification.getId());
                userState.setUserId(userId);
                userState.setIsRead(false);
                userState.setIsDelete(false);
                userState.setCreateTime(now);
                notificationUserStateMapper.insert(userState);
            }
        }
    }

    /**
     * 根据目标类型和目标ID列表解析出实际用户ID列表
     *
     * @param targetType 目标类型（0-全部 1-部门 2-角色 3-指定用户）
     * @param targetIds  目标ID列表
     * @return 去重后的用户ID列表
     */
    private List<Long> resolveTargetUserIds(Integer targetType, List<Long> targetIds) {
        Set<Long> userIdSet = new HashSet<>();

        switch (targetType) {
            case 0 -> {
                // 全部用户
                List<SysUser> allUsers = sysUserMapper.selectList(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::isDelFlag, false)
                                .eq(SysUser::getStatus, 1)
                                .select(SysUser::getUserId)
                );
                allUsers.forEach(u -> userIdSet.add(u.getUserId()));
            }
            case 1 -> {
                // 按部门：查询部门下所有用户
                if (targetIds != null && !targetIds.isEmpty()) {
                    List<SysUserDept> userDepts = sysUserDeptMapper.selectList(
                            new LambdaQueryWrapper<SysUserDept>()
                                    .in(SysUserDept::getDeptId, targetIds)
                    );
                    userDepts.forEach(ud -> userIdSet.add(ud.getUserId()));
                }
            }
            case 2 -> {
                // 按角色：查询角色下所有用户
                if (targetIds != null && !targetIds.isEmpty()) {
                    List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                            new LambdaQueryWrapper<SysUserRole>()
                                    .in(SysUserRole::getRoleId, targetIds)
                    );
                    userRoles.forEach(ur -> userIdSet.add(ur.getUserId()));
                }
            }
            case 3 -> {
                // 指定用户
                if (targetIds != null) {
                    userIdSet.addAll(targetIds);
                }
            }
            default -> {}
        }

        return new ArrayList<>(userIdSet);
    }

    /**
     * 修改通知
     * @param dto 通知DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotification(NotificationDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("通知ID不能为空");
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException("通知类型不能为空");
        }
        if (dto.getTargetType() == null) {
            throw new IllegalArgumentException("通知目标范围不能为空");
        }
        // 非全部时，目标ID列表不能为空
        if (!Integer.valueOf(0).equals(dto.getTargetType())
                && (dto.getTargetIds() == null || dto.getTargetIds().isEmpty())) {
            throw new IllegalArgumentException("请选择通知目标");
        }

        // 查询原通知信息
        Notification existingNotification = notificationMapper.selectById(dto.getId());
        if (existingNotification == null) {
            throw new IllegalArgumentException("通知不存在");
        }

        Integer oldStatus = existingNotification.getStatus();

        // 更新通知主表
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setTitle(dto.getTitle());
        notification.setSummary(dto.getSummary());
        notification.setContent(dto.getContent());
        notification.setType(dto.getType());
        notification.setUpdateBy(SecurityUtils.getUserName());
        notification.setUpdateTime(new Date());

        // 根据状态判断：1-立即发布，0-存草稿
        if (Integer.valueOf(1).equals(dto.getStatus())) {
            notification.setStatus(1);
            // 如果原来是草稿，现在发布，设置发布时间
            if (!Integer.valueOf(1).equals(oldStatus)) {
                notification.setPublishTime(new Date());
            }
        } else {
            notification.setStatus(0);
            // 如果从发布改为草稿，清空发布时间
            if (Integer.valueOf(1).equals(oldStatus)) {
                notification.setPublishTime(null);
            }
        }

        notificationMapper.updateById(notification);

        // 删除旧的通知目标关系
        notificationTargetMapper.delete(
                new LambdaQueryWrapper<NotificationTarget>()
                        .eq(NotificationTarget::getNotificationId, dto.getId())
        );

        // 插入新的通知目标表
        if (Integer.valueOf(0).equals(dto.getTargetType())) {
            // 全部用户：插入一条 targetType=0, targetId=null 的记录
            NotificationTarget target = new NotificationTarget();
            target.setNotificationId(dto.getId());
            target.setTargetType(0);
            notificationTargetMapper.insert(target);
        } else {
            // 部门/角色/指定用户：每个目标ID插一条记录
            for (Long targetId : dto.getTargetIds()) {
                NotificationTarget target = new NotificationTarget();
                target.setNotificationId(dto.getId());
                target.setTargetType(dto.getTargetType());
                target.setTargetId(targetId);
                notificationTargetMapper.insert(target);
            }
        }

        // 如果是已发布状态，重新生成用户通知状态
        if (Integer.valueOf(1).equals(dto.getStatus())) {
            // 删除旧的用户通知状态
            notificationUserStateMapper.delete(
                    new LambdaQueryWrapper<NotificationUserState>()
                            .eq(NotificationUserState::getNotificationId, dto.getId())
            );

            // 解析目标用户并批量插入用户通知状态
            List<Long> userIds = resolveTargetUserIds(dto.getTargetType(), dto.getTargetIds());
            Date now = new Date();
            for (Long userId : userIds) {
                NotificationUserState userState = new NotificationUserState();
                userState.setNotificationId(dto.getId());
                userState.setUserId(userId);
                userState.setIsRead(false);
                userState.setIsDelete(false);
                userState.setCreateTime(now);
                notificationUserStateMapper.insert(userState);
            }
        } else if (Integer.valueOf(1).equals(oldStatus)) {
            // 如果从发布改为草稿，删除所有用户通知状态
            notificationUserStateMapper.delete(
                    new LambdaQueryWrapper<NotificationUserState>()
                            .eq(NotificationUserState::getNotificationId, dto.getId())
            );
        }
    }

    /**
     * 发布通知（将草稿状态改为已发布）
     * @param notificationId 通知ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotification(Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("通知ID不能为空");
        }

        // 查询通知
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("通知不存在");
        }

        // 检查状态
        if (Integer.valueOf(1).equals(notification.getStatus())) {
            throw new IllegalArgumentException("通知已发布，无需重复操作");
        }

        // 更新通知状态为已发布
        Notification updateNotification = new Notification();
        updateNotification.setId(notificationId);
        updateNotification.setStatus(1);
        updateNotification.setPublishTime(new Date());
        updateNotification.setUpdateBy(SecurityUtils.getUserName());
        updateNotification.setUpdateTime(new Date());
        notificationMapper.updateById(updateNotification);

        // 查询通知目标信息
        List<NotificationTarget> targets = notificationTargetMapper.selectList(
                new LambdaQueryWrapper<NotificationTarget>()
                        .eq(NotificationTarget::getNotificationId, notificationId)
        );

        if (targets.isEmpty()) {
            throw new IllegalArgumentException("通知目标信息缺失");
        }

        // 解析目标类型和目标ID
        NotificationTarget firstTarget = targets.get(0);
        Integer targetType = firstTarget.getTargetType();
        List<Long> targetIds = null;

        if (!Integer.valueOf(0).equals(targetType)) {
            targetIds = targets.stream()
                    .map(NotificationTarget::getTargetId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 解析目标用户并生成用户通知状态
        List<Long> userIds = resolveTargetUserIds(targetType, targetIds);
        Date now = new Date();
        for (Long userId : userIds) {
            NotificationUserState userState = new NotificationUserState();
            userState.setNotificationId(notificationId);
            userState.setUserId(userId);
            userState.setIsRead(false);
            userState.setIsDelete(false);
            userState.setCreateTime(now);
            notificationUserStateMapper.insert(userState);
        }
    }
}