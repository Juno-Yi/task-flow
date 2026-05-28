package com.junoyi.task.service.impl;

import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.exception.TaskException;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import com.junoyi.task.service.IMyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}