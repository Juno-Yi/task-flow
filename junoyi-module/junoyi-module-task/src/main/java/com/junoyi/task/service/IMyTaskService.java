package com.junoyi.task.service;

import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;

import java.util.List;

/**
 * 我的任务业务接口
 *
 * @author Fan
 */
public interface IMyTaskService {

    /**
     * 获取当前月的任务列表
     * @return 任务列表
     */
    List<TaskItemVO> getCurrentMonthMyTask(Long userId);

    /**
     * 获取我的任务详情
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务详情
     */
    TaskListDetailVO getMyTaskDetail(Long taskId, Long userId);
}