package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.task.domain.dto.TaskListDTO;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.po.TaskUser;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.exception.TaskException;
import com.junoyi.task.mapper.TaskListMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import com.junoyi.task.service.ITaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务列表业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements ITaskListService {

    private final TaskListMapper taskListMapper;
    private final TaskUserMapper taskUserMapper;
    private final SysDictApi sysDictApi;
    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TaskListVO> getTaskList(TaskListQueryDTO queryDTO, Page<Task> page) {
        // 使用 XML 中定义的 SQL 查询
        IPage<TaskListVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<TaskListVO> resultPage = taskListMapper.selectTaskListPage(voPage, queryDTO);

        List<TaskListVO> records = resultPage.getRecords();

        if (records.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 批量查询协作人列表
        List<Long> taskIds = records.stream().map(TaskListVO::getId).collect(Collectors.toList());
        Map<Long, List<TaskListVO.TaskUser>> taskUsersMap = batchQueryTaskUsers(taskIds);

        // 批量获取字典数据
        Map<String, SysDictDataVO> statusMap = buildDictMap("task_status");
        Map<String, SysDictDataVO> priorityMap = buildDictMap("task_priority");

        // 填充字典标签和协作人列表
        for (TaskListVO vo : records) {
            // 填充协作人列表
            vo.setTaskUserList(taskUsersMap.getOrDefault(vo.getId(), new ArrayList<>()));

            // 填充状态标签
            if (vo.getStatus() != null) {
                SysDictDataVO statusDict = statusMap.get(String.valueOf(vo.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            // 填充优先级标签
            if (vo.getPriority() != null) {
                SysDictDataVO priorityDict = priorityMap.get(String.valueOf(vo.getPriority()));
                if (priorityDict != null) {
                    vo.setPriorityLabel(priorityDict.getDictLabel());
                    vo.setPriorityType(priorityDict.getListClass());
                }
            }
        }

        return PageResult.of(records, resultPage.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 批量查询任务协作人
     *
     * @param taskIds 任务ID列表
     * @return 任务ID为key，协作人列表为value的Map
     */
    private Map<Long, List<TaskListVO.TaskUser>> batchQueryTaskUsers(List<Long> taskIds) {
        if (taskIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        // 查询协作人关联关系（taskRole = 2 表示协作人）
        LambdaQueryWrapper<TaskUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TaskUser::getTaskId, taskIds)
                .eq(TaskUser::getTaskRole, 2);
        List<TaskUser> taskUsers = taskUserMapper.selectList(wrapper);

        if (taskUsers.isEmpty()) {
            return new java.util.HashMap<>();
        }

        // 批量查询用户信息
        List<Long> userIds = taskUsers.stream()
                .map(TaskUser::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getUserId, u -> u));

        // 按任务ID分组
        return taskUsers.stream()
                .collect(Collectors.groupingBy(
                        TaskUser::getTaskId,
                        Collectors.mapping(
                                tu -> {
                                    TaskListVO.TaskUser taskUser = new TaskListVO.TaskUser();
                                    SysUser user = userMap.get(tu.getUserId());
                                    if (user != null) {
                                        taskUser.setUserId(user.getUserId());
                                        taskUser.setAvatar(user.getAvatar());
                                        taskUser.setNickName(user.getNickName());
                                    }
                                    return taskUser;
                                },
                                Collectors.toList()
                        )
                ));
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
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    @Override
    public TaskListDetailVO getTaskDetail(Long taskId) {
        if (taskId == null) {
            throw new TaskException("任务ID不能为空");
        }
        TaskListDetailVO detailVO = taskListMapper.selectTaskDetailById(taskId);
        if (detailVO == null) {
            throw new TaskException("任务不存在");
        }
        if (detailVO.getRecordList() != null && !detailVO.getRecordList().isEmpty()) {
            for (TaskListDetailVO.RecordItem recordItem : detailVO.getRecordList()) {
                if (recordItem == null || recordItem.getActionType() == null) {
                    continue;
                }
                if (recordItem.getActionType() == 1) {
                    recordItem.setActionTypeLabel("提交任务");
                    if (detailVO.getLatestSubmitRecord() == null) {
                        detailVO.setLatestSubmitRecord(recordItem);
                    }
                } else if (recordItem.getActionType() == 2) {
                    recordItem.setActionTypeLabel("驳回任务");
                    if (detailVO.getLatestRejectRecord() == null) {
                        detailVO.setLatestRejectRecord(recordItem);
                    }
                } else if (recordItem.getActionType() == 3) {
                    recordItem.setActionTypeLabel("审核通过");
                } else {
                    recordItem.setActionTypeLabel("任务操作");
                }
            }
        }
        return detailVO;
    }

    /**
     * 添加任务
     * @param dto 任务DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTask(TaskListDTO dto) {
        if (dto == null) {
            throw new TaskException("任务数据不能为空");
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new TaskException("任务标题不能为空");
        }
        if (dto.getOwnerUserId() == null) {
            throw new TaskException("负责人不能为空");
        }

        Task task = new Task();
        Date now = DateUtils.getNowDate();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
//        task.setDueTime(dto.getDueTime());
        task.setRemark(dto.getRemark());

        // 默认值
        task.setStatus(0);
        task.setDelFlag(false);
        task.setType(0);
        task.setCreatorId(SecurityUtils.getUserId());
        task.setCreateBy(SecurityUtils.getUserName());
        task.setCreateTime(now);

        taskListMapper.insert(task);

        // 插入项目执行员（用户id、加角色）
        // 优先插入负责人
        TaskUser ownerRel = new TaskUser();
        ownerRel.setTaskId(task.getId());
        ownerRel.setUserId(dto.getOwnerUserId());
        ownerRel.setTaskRole(2);
        ownerRel.setCreateTime(now);
        taskUserMapper.insert(ownerRel);
        // 再插入执行员（去重，避免重复）
        if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
            for (Long userId : dto.getUserIds()) {
                if (userId == null) {
                    continue;
                }
                if (userId.equals(dto.getOwnerUserId())) {
                    continue; // 负责人已插入，不重复插
                }

                TaskUser executorRel = new TaskUser();
                executorRel.setTaskId(task.getId());
                executorRel.setUserId(userId);
                executorRel.setTaskRole(1);
                executorRel.setCreateTime(now);
                taskUserMapper.insert(executorRel);
            }
        }
        List<Long> eventUserIds = dto.getUserIds() == null ? List.of() : new ArrayList<>(dto.getUserIds());
        // TODO: 任务创建后发送企业微信、飞书、钉钉通知
        //        EventBus.get().callEvent(new TaskCreatedEvent(
//                task.getId(),
//                task.getTitle(),
//                task.getDescription(),
//                task.getRemark(),
//                task.getCreateTime(),
//                task.getDueTime(),
//                dto.getOwnerUserId(),
//                eventUserIds,
//                dto.getSyncSchedule()
//        ));

        // 发布操作日志
        EventBus.get().callEvent(UserOperationEvent.withRawData(
                "create",
                "task",
                "创建了任务「" + task.getTitle() + "」",
                String.valueOf(task.getId()),
                task.getTitle(),
                JsonUtils.toJsonString(dto)
        ));
    }


    /**
     * 修改任务
     * @param dto 任务DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(TaskListDTO dto){
        if (dto == null || dto.getId() == null) {
            throw new TaskException("任务ID不能为空");
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new TaskException("任务标题不能为空");
        }
        if (dto.getOwnerUserId() == null) {
            throw new TaskException("负责人不能为空");
        }

        Task existTask = taskListMapper.selectById(dto.getId());
        if (existTask == null || Boolean.TRUE.equals(existTask.getDelFlag())) {
            throw new TaskException("任务不存在");
        }

        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
//        task.setDueTime(dto.getDueTime());
        task.setRemark(dto.getRemark());
        task.setUpdateBy(SecurityUtils.getUserName());
        task.setUpdateTime(DateUtils.getNowDate());
        taskListMapper.updateById(task);

        LambdaQueryWrapper<TaskUser> removeWrapper = new LambdaQueryWrapper<>();
        removeWrapper.eq(TaskUser::getTaskId, dto.getId());
        taskUserMapper.delete(removeWrapper);

        Date now = DateUtils.getNowDate();
        TaskUser ownerRel = new TaskUser();
        ownerRel.setTaskId(dto.getId());
        ownerRel.setUserId(dto.getOwnerUserId());
        ownerRel.setTaskRole(2);
        ownerRel.setCreateTime(now);
        taskUserMapper.insert(ownerRel);

        if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
            for (Long userId : dto.getUserIds()) {
                if (userId == null || userId.equals(dto.getOwnerUserId())) {
                    continue;
                }
                TaskUser executorRel = new TaskUser();
                executorRel.setTaskId(dto.getId());
                executorRel.setUserId(userId);
                executorRel.setTaskRole(1);
                executorRel.setCreateTime(now);
                taskUserMapper.insert(executorRel);
            }
        }

        List<Long> eventUserIds = dto.getUserIds() == null ? List.of() : new ArrayList<>(dto.getUserIds());
        // TODO: 任务更新修改后发送企业微信、飞书、钉钉通知
//        EventBus.get().callEvent(new TaskUpdatedEvent(
//                existTask.getId(),
//                existTask.getWecomScheduleId(),
//                dto.getTitle(),
//                dto.getDescription(),
//                dto.getRemark(),
//                existTask.getCreateTime(),
//                dto.getDueTime(),
//                dto.getOwnerUserId(),
//                eventUserIds,
//                dto.getSyncSchedule()
//        ));

        EventBus.get().callEvent(UserOperationEvent.withRawData(
                "update",
                "task",
                "更新了任务「" + dto.getTitle() + "」",
                String.valueOf(dto.getId()),
                dto.getTitle(),
                JsonUtils.toJsonString(dto)
        ));
    }

    /**
     * 催促提醒用户完成任务
     *
     * @param taskId 任务ID
     */
    @Override
    public void remindUserToCompleteTask(Long taskId) {
        if (taskId == null || taskId <= 0) {
            throw new TaskException("任务ID不能为空");
        }
        TaskListDetailVO detailVO = getTaskDetail(taskId);
        if (detailVO.getStatus() == null) {
            throw new TaskException("任务状态异常");
        }
        if (Integer.valueOf(4).equals(detailVO.getStatus())) {
            throw new TaskException("当前任务已完成，无需催办");
        }
        List<Long> userIds = detailVO.getTaskUserList() == null ? List.of() : detailVO.getTaskUserList().stream()
                .map(TaskListDetailVO.TaskUser::getUserId)
                .filter(userId -> userId != null && !userId.equals(detailVO.getOwnerUser() == null ? null : detailVO.getOwnerUser().getUserId()))
                .toList();
//        EventBus.get().callEvent(new TaskRemindEvent(
//                detailVO.getId(),
//                detailVO.getTitle(),
//                detailVO.getRemark(),
//                detailVO.getDueTime(),
//                detailVO.getOwnerUser() == null ? null : detailVO.getOwnerUser().getUserId(),
//                userIds,
//                detailVO.getStatus()
//        ));
    }
}