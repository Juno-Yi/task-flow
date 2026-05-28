package com.junoyi.task.service.impl;

import com.junoyi.framework.core.domain.module.R;
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
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务详情
     */
    @Override
    public TaskListDetailVO getMyTaskDetail(Long taskId, Long userId) {
        return null;
    }
}