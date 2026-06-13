package com.junoyi.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.task.domain.bo.TaskActionBO;
import com.junoyi.task.domain.po.Task;
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
     * 获取当前月的任务列表（按状态分页查询）
     * @param userId 用户ID
     * @param status 任务状态
     * @param page 分页对象
     * @return 分页任务列表
     */
    PageResult<TaskItemVO> getCurrentMonthMyTaskByStatus(Long userId, Integer status, Page<Task> page);

    /**
     * 获取我的任务详情
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务详情
     */
    TaskListDetailVO getMyTaskDetail(Long taskId, Long userId);

    /**
     * 开始任务
     * @param taskId 任务ID
     */
    void startTask(Long taskId);

    /**
     * 提交任务
     *
     * @param bo 任务操作BO
     */
    void submitTask(TaskActionBO bo);
}