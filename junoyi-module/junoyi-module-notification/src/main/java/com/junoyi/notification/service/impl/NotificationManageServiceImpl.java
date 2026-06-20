package com.junoyi.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.notification.converter.NotificationConverter;
import com.junoyi.notification.domain.dto.NotificationDTO;
import com.junoyi.notification.domain.po.Notification;
import com.junoyi.notification.domain.vo.NotificationListVO;
import com.junoyi.notification.mapper.NotificationMapper;
import com.junoyi.notification.service.INotificationManageService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private final SysUserMapper sysUserMapper;
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
     * 添加通知（立即发布或存储草稿）
     * status: 0-草稿  1-已发布
     * @param dto 通知DTO
     */
    @Override
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

        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setType(dto.getType());
        notification.setSenderId(SecurityUtils.getUserId());
        notification.setCreateBy(SecurityUtils.getUserName());
        notification.setCreateTime(new Date());

        // 根据状态判断：1-立即发布，0-存草稿
        if (Integer.valueOf(1).equals(dto.getStatus())) {
            notification.setStatus(1);
            notification.setPublishTime(new Date());

            // TODO: 发布通知
        } else {
            notification.setStatus(0);
        }

        notificationMapper.insert(notification);
    }
}