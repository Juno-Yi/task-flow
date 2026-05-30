package com.junoyi.task.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.po.TaskRecord;
import com.junoyi.task.domain.po.TaskUser;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import com.junoyi.task.enums.TaskRecordActionType;
import com.junoyi.task.exception.TaskException;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskRecordMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 任务业务API接口实现
 * 任务模块中实现业务接口
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskServiceApiImpl implements ITaskServiceApi {

    private final TaskMapper taskMapper;
    private final TaskUserMapper taskUserMapper;
    private final TaskRecordMapper taskRecordMapper;

    /**
     * 获取项目任务列表
     * @param projectId 项目ID
     * @return 项目任务列表
     */
    @Override
    public List<ProjectTaskItemVO> getProjectTaskList(Long projectId) {
        // 参数校验
        if (projectId == null) {
            throw new TaskException("项目ID不能为空");
        }

        // 查询项目任务列表
        return taskMapper.selectProjectTaskList(projectId);
    }

    /**
     * 创建项目任务
     * @param projectTaskCreateDTO 项目任务创建数据传递对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProjectTask(ProjectTaskCreateDTO projectTaskCreateDTO) {
        // 参数校验
        validateProjectTaskCreateDTO(projectTaskCreateDTO);
        // 构建任务实体
        Task task = buildTaskFromDTO(projectTaskCreateDTO);
        // 插入任务记录
        taskMapper.insert(task);
        // 插入任务负责人关联
        insertOwnerUser(task.getId(), projectTaskCreateDTO.getOwnerUserId());
        // 插入任务协作人关联
        insertTaskUsers(task.getId(), projectTaskCreateDTO.getUserIds(), projectTaskCreateDTO.getOwnerUserId());

        // 插入任务创建记录
//        insertTaskRecord(task.getId(), TaskRecordActionType.CREATE, "创建项目任务");
    }

    /**
     * 校验项目任务创建DTO
     *
     * @param dto 项目任务创建DTO
     */
    private void validateProjectTaskCreateDTO(ProjectTaskCreateDTO dto) {
        if (dto == null) {
            throw new TaskException("任务数据不能为空");
        }
        if (dto.getProjectId() == null) {
            throw new TaskException("项目ID不能为空");
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new TaskException("任务标题不能为空");
        }
        if (dto.getOwnerUserId() == null) {
            throw new TaskException("任务负责人不能为空");
        }
    }

    /**
     * 从DTO构建Task实体
     *
     * @param dto 项目任务创建DTO
     * @return Task实体
     */
    private Task buildTaskFromDTO(ProjectTaskCreateDTO dto) {
        Date now = DateUtils.getNowDate();
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserName = SecurityUtils.getUserName();

        Task task = new Task();
        task.setProjectId(dto.getProjectId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        task.setRemark(dto.getRemark());
        task.setPlanEndTime(dto.getPlanStartTime());
        task.setPlanStartTime(dto.getPlanEndTime());

        // 设置默认值
        task.setStatus(0);          // 待开始
        task.setType(1);            // 项目任务
        task.setDelFlag(false);     // 未删除
        task.setCreatorId(currentUserId);
        task.setCreateBy(currentUserName);
        task.setCreateTime(now);

        return task;
    }

    /**
     * 插入任务负责人关联
     *
     * @param taskId 任务ID
     * @param ownerUserId 负责人ID
     */
    private void insertOwnerUser(Long taskId, Long ownerUserId) {
        Date now = DateUtils.getNowDate();

        TaskUser ownerRel = new TaskUser();
        ownerRel.setTaskId(taskId);
        ownerRel.setUserId(ownerUserId);
        ownerRel.setTaskRole(1);  // 负责人角色
        ownerRel.setCreateTime(now);

        taskUserMapper.insert(ownerRel);
    }

    /**
     * 插入任务协作人关联
     *
     * @param taskId 任务ID
     * @param userIds 协作人ID列表
     * @param ownerUserId 负责人ID（用于去重）
     */
    private void insertTaskUsers(Long taskId, List<Long> userIds, Long ownerUserId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        Date now = DateUtils.getNowDate();

        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            // 跳过负责人（避免重复）
            if (userId.equals(ownerUserId)) {
                continue;
            }

            TaskUser executorRel = new TaskUser();
            executorRel.setTaskId(taskId);
            executorRel.setUserId(userId);
            executorRel.setTaskRole(2);  // 协作人角色
            executorRel.setCreateTime(now);

            taskUserMapper.insert(executorRel);
        }
    }

    /**
     * 插入任务操作记录
     *
     * @param taskId 任务ID
     * @param actionType 操作类型
     * @param remark 备注
     */
    private void insertTaskRecord(Long taskId, TaskRecordActionType actionType, String remark) {
        Date now = DateUtils.getNowDate();
        Long currentUserId = SecurityUtils.getUserId();

        TaskRecord record = new TaskRecord();
        record.setTaskId(taskId);
        record.setOperatorId(currentUserId);
        record.setActionType(actionType.getValue());
        record.setRemark(remark);
        record.setCreateTime(now);

        taskRecordMapper.insert(record);
    }

    /**
     * 更新项目任务
     * @param projectTaskUpdateDTO 项目任务更新数据传递对象
     */
    @Override
    public void updateProjectTask(ProjectTaskUpdateDTO projectTaskUpdateDTO) {

    }

    /**
     * TODO 根据任务ID获取项目ID
     * @param taskId 任务ID
     * @return 项目ID，如果任务不存在或不是项目任务则返回null
     */
    @Override
    public Long getProjectIdByTaskId(Long taskId) {
        return 0L;
    }

    /**
     * 获取项目任务总数量
     * @param projectId 项目ID
     * @return 任务总数
     */
    @Override
    public Long getProjectTaskCount(Long projectId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getProjectId, projectId)
                .eq(Task::getDelFlag, false);
        return taskMapper.selectCount(wrapper);
    }

    /**
     * 获取项目未完成的任务总数量
     * @param projectId 项目ID
     * @return 未完成任务总数
     */
    @Override
    public Long getProjectUnfinishedTaskCount(Long projectId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getProjectId, projectId)
                .eq(Task::getDelFlag, false)
                .in(Task::getStatus, 0,1,2,3);
        return taskMapper.selectCount(wrapper);
    }
}