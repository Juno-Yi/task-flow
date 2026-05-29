package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.task.domain.bo.TaskActionBO;
import com.junoyi.task.domain.dto.TaskApprovalDTO;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.po.TaskRecord;
import com.junoyi.task.domain.po.TaskUser;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.exception.TaskException;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskRecordMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import com.junoyi.task.service.ITaskApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务审核业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskApprovalServiceImpl implements ITaskApprovalService {

    private final TaskMapper taskMapper;
    private final TaskUserMapper taskUserMapper;
    private final SysDictApi sysDictApi;
    private final SysUserMapper sysUserMapper;
    private final TaskRecordMapper taskRecordMapper;

    /**
     * 获取任务审核列表
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 任务审核列表
     */
    @Override
    public PageResult<TaskListVO> getApprovalList(TaskListQueryDTO queryDTO, Page<Task> page) {
        // 强制设置状态为待验收（status = 2）
        if (queryDTO == null) {
            queryDTO = new TaskListQueryDTO();
        }
        queryDTO.setStatus(2);

        // 使用 XML 中定义的 SQL 查询
        IPage<TaskListVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<TaskListVO> resultPage = taskMapper.selectTaskListPage(voPage, queryDTO);

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
     * 通过任务审核
     *
     * 通过任务审核后，将最后一次提交的时间，设置为实际完成时间
     * @param bo 业务数据对象
     */
    @Override
    public void passTask(TaskActionBO bo) {
        TaskApprovalDTO dto = validateApprovalAction(bo);
        // 获取任务最后一次提交的时间
        Date latestSubmitTime = getLatestSubmitTime(dto.getTaskId());
        Task updateTask = new Task();
        updateTask.setId(dto.getTaskId());
        updateTask.setStatus(4);
        updateTask.setEndTime(latestSubmitTime == null ? DateUtils.getNowDate() : latestSubmitTime);
        updateTask.setUpdateBy(SecurityUtils.getUserName());
        updateTask.setUpdateTime(DateUtils.getNowDate());
        int rows = taskMapper.updateById(updateTask);
        if (rows <= 0) {
            throw new TaskException("审核通过失败，任务数据未更新");
        }
        saveTaskRecord(dto.getTaskId(), bo.getUserId(), bo.getTaskActionType(), dto.getRemark());
    }

    /**
     * 验证审核操作参数
     * @param bo 业务数据
     * @return 返回审核DTO
     */
    private TaskApprovalDTO validateApprovalAction(TaskActionBO bo) {
        if (bo == null) {
            throw new TaskException("审核参数不能为空");
        }
        if (bo.getUserId() == null || bo.getUserId() <= 0) {
            throw new TaskException("审核用户不能为空");
        }
        if (!(bo.getDto() instanceof TaskApprovalDTO dto)) {
            throw new TaskException("审核数据不能为空");
        }
        if (dto.getTaskId() == null || dto.getTaskId() <= 0) {
            throw new TaskException("任务ID不能为空");
        }
        Task existTask = taskMapper.selectById(dto.getTaskId());
        if (existTask == null || Boolean.TRUE.equals(existTask.getDelFlag())) {
            throw new TaskException("任务不存在");
        }
        if (existTask.getStatus() == null) {
            throw new TaskException("任务状态异常");
        }
        if (!Integer.valueOf(2).equals(existTask.getStatus())) {
            throw new TaskException("当前任务不是待验收状态，无法审核");
        }
        return dto;
    }

    /**
     * 获取任务最后一次提交的时间
     * @param taskId 任务ID
     * @return 最后一次提交的时间
     */
    private Date getLatestSubmitTime(Long taskId) {
        TaskRecord latestSubmitRecord = taskRecordMapper.selectOne(new LambdaQueryWrapper<TaskRecord>()
                .eq(TaskRecord::getTaskId, taskId)
                // 类型1为提交任务
                .eq(TaskRecord::getActionType, 1)
                .orderByDesc(TaskRecord::getCreateTime)
                .last("limit 1"));
        return latestSubmitRecord == null ? null : latestSubmitRecord.getCreateTime();
    }

    /**
     * 保存任务记录
     * @param taskId 任务ID
     * @param userId 任务操作用户ID
     * @param actionType 操作类型
     * @param remark 备注
     */
    private void saveTaskRecord(Long taskId, Long userId, Integer actionType, String remark) {
        TaskRecord record = new TaskRecord();
        record.setTaskId(taskId);
        record.setOperatorId(userId);
        record.setActionType(actionType);
        record.setRemark(remark);
        record.setCreateTime(DateUtils.getNowDate());
        int recordRows = taskRecordMapper.insert(record);
        if (recordRows <= 0) {
            throw new TaskException("任务审核失败，任务记录保存失败");
        }
    }
}