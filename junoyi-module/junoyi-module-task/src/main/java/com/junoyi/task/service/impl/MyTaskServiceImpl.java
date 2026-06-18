package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.task.domain.bo.TaskActionBO;
import com.junoyi.task.domain.dto.TaskSubmitDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.po.TaskAttachment;
import com.junoyi.task.domain.po.TaskRecord;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskMonthStatisticsVO;
import com.junoyi.task.exception.TaskException;
import com.junoyi.task.mapper.TaskAttachmentMapper;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskRecordMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import com.junoyi.task.service.IMyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 我的任务业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class MyTaskServiceImpl implements IMyTaskService {

    private final TaskMapper taskMapper;
    private final TaskRecordMapper taskRecordMapper;
    private final TaskAttachmentMapper taskAttachmentMapper;
    private final TaskUserMapper taskUserMapper;


    /**
     * 获取当前月的任务列表
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    @Override
    public List<TaskItemVO> getCurrentMonthMyTask(Long userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new TaskException("用户ID不能为空");
        }

        // 计算当前月的起止时间
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int daysOfMonth = currentDate.lengthOfMonth();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(daysOfMonth);

        LocalDateTime monthStartDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEndDateTime = lastDayOfMonth.atTime(LocalTime.MAX);

        Date monthStart = Date.from(monthStartDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date monthEnd = Date.from(monthEndDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // 查询当前月的任务列表
        List<TaskItemVO> taskList = taskMapper.selectCurrentMonthTaskList(userId, monthStart, monthEnd);

        // 返回结果（如果为空返回空列表）
        return taskList != null ? taskList : List.of();
    }

    /**
     * 获取当前月的任务列表（按状态分页查询）
     *
     * @param userId 用户ID
     * @param status 任务状态
     * @param page   分页对象
     * @return 分页任务列表
     */
    @Override
    public PageResult<TaskItemVO> getCurrentMonthMyTaskByStatus(Long userId, Integer status, Page<Task> page) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new TaskException("用户ID不能为空");
        }
        if (status == null) {
            throw new TaskException("任务状态不能为空");
        }

        // 计算当前月的起止时间
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int daysOfMonth = currentDate.lengthOfMonth();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(daysOfMonth);

        LocalDateTime monthStartDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEndDateTime = lastDayOfMonth.atTime(LocalTime.MAX);

        Date monthStart = Date.from(monthStartDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date monthEnd = Date.from(monthEndDateTime.atZone(ZoneId.systemDefault()).toInstant());

        IPage<TaskItemVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<TaskItemVO> resultPage = taskMapper.selectCurrentMonthTaskListByStatusPage(voPage, userId, status, monthStart, monthEnd);

        // 封装分页结果
        return PageResult.of(
                resultPage.getRecords(),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    /**
     * 获取我的任务详情
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务详情
     */
    @Override
    public TaskListDetailVO getMyTaskDetail(Long taskId, Long userId) {
        //  参数校验
        if (taskId == null || taskId <= 0) {
            throw new TaskException("任务ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new TaskException("用户ID不能为空");
        }

        // 验证用户是否有权限查看该任务（是负责人或协作人）
        Long relationCount = taskMapper.countTaskUserRelation(taskId, userId);
        if (relationCount == null || relationCount == 0) {
            throw new TaskException("无权限查看该任务");
        }

        // 查询任务详情
        TaskListDetailVO detailVO = taskMapper.selectTaskDetailById(taskId);
        if (detailVO == null) {
            throw new TaskException("任务不存在");
        }

        // 返回任务详情
        return detailVO;
    }

    /**
     * 开始任务
     * @param taskId 任务ID
     */
    @Override
    public void startTask(Long taskId) {
        if (taskId == null || taskId <= 0) {
            throw new TaskException("任务ID不能为空");
        }

        Task existTask = taskMapper.selectById(taskId);
        if (existTask == null || Boolean.TRUE.equals(existTask.getDelFlag())) {
            throw new TaskException("任务不存在");
        }
        if (existTask.getStatus() == null) {
            throw new TaskException("任务状态异常");
        }
        if (!Integer.valueOf(0).equals(existTask.getStatus())) {
            throw new TaskException("当前任务不是待处理状态，无法开始");
        }

        Task updateTask = new Task();
        updateTask.setId(taskId);
        // 状态1为进行中
        updateTask.setStatus(1);
        updateTask.setStartTime(existTask.getStartTime() == null ? DateUtils.getNowDate() : existTask.getStartTime());
        updateTask.setUpdateBy(SecurityUtils.getUserName());
        updateTask.setUpdateTime(DateUtils.getNowDate());
        taskMapper.updateById(updateTask);
    }

    /**
     * 提交任务
     *
     * @param bo 任务操作BO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitTask(TaskActionBO bo) {
        if (bo == null) {
            throw new TaskException("提交参数不能为空");
        }
        if (bo.getUserId() == null || bo.getUserId() <= 0) {
            throw new TaskException("提交用户不能为空");
        }
        TaskSubmitDTO dto = (TaskSubmitDTO) bo.getDto();
        if (dto == null) {
            throw new TaskException("任务提交数据不能为空");
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
        if (!Integer.valueOf(1).equals(existTask.getStatus()) && !Integer.valueOf(3).equals(existTask.getStatus())) {
            throw new TaskException("当前任务不是进行中或已驳回状态，无法提交");
        }

        Task updateTask = new Task();
        updateTask.setId(dto.getTaskId());
        updateTask.setStatus(2);
        updateTask.setUpdateBy(SecurityUtils.getUserName());
        updateTask.setUpdateTime(DateUtils.getNowDate());
        int rows = taskMapper.updateById(updateTask);
        if (rows <= 0) {
            throw new TaskException("提交任务失败，任务数据未更新");
        }

        TaskRecord record = new TaskRecord();
        record.setTaskId(dto.getTaskId());
        record.setOperatorId(bo.getUserId());
        record.setActionType(bo.getTaskActionType());
        record.setRemark(dto.getRemark());
        record.setCreateTime(DateUtils.getNowDate());
        int recordRows = taskRecordMapper.insert(record);
        if (recordRows <= 0 || record.getId() == null) {
            throw new TaskException("提交任务失败，任务记录保存失败");
        }
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (TaskSubmitDTO.Attachment attachmentDTO : dto.getAttachments()) {
                if (attachmentDTO == null) {
                    continue;
                }
                TaskAttachment attachment = new TaskAttachment();
                attachment.setTaskId(dto.getTaskId());
                attachment.setRecordId(record.getId());
                attachment.setFileName(attachmentDTO.getFileName());
                attachment.setFileUrl(attachmentDTO.getFileUrl());
                attachment.setUploadUser(bo.getUserId());
                attachment.setCreateTime(DateUtils.getNowDate());
                int attachmentRows = taskAttachmentMapper.insert(attachment);
                if (attachmentRows <= 0) {
                    throw new TaskException("提交任务失败，附件保存失败");
                }
            }
        }

    }

    /**
     * 获取用户当前月任务统计数据
     * @param userId 用户ID
     * @return 当前月任务统计数据
     */
    @Override
    public TaskMonthStatisticsVO getTaskMonthStatistics(Long userId) {
        if (userId == null || userId <= 0) {
            throw new TaskException("用户ID不能为空");
        }

        // 计算当前月的起止时间
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        LocalDateTime monthStartDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime monthEndDateTime = lastDayOfMonth.atTime(LocalTime.MAX);

        Date monthStart = Date.from(monthStartDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date monthEnd = Date.from(monthEndDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // 查询当前月用户参与的所有任务（未完成 + 当月已完成）
        List<TaskItemVO> taskList = taskMapper.selectCurrentMonthTaskList(userId, monthStart, monthEnd);

        // 统计各状态数量
        int pendingCount = 0;
        int completedCount = 0;

        if (taskList != null) {
            for (TaskItemVO task : taskList) {
                if (Integer.valueOf(4).equals(task.getStatus())) {
                    completedCount++;
                } else {
                    pendingCount++;
                }
            }
        }

        // 封装结果
        TaskMonthStatisticsVO vo = new TaskMonthStatisticsVO();
        vo.setPendingTaskCount(pendingCount);
        vo.setCompletedTaskCount(completedCount);
        vo.setMonthTaskCount(pendingCount + completedCount);

        return vo;
    }
}