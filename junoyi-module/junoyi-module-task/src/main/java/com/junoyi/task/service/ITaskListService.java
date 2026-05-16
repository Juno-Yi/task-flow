package com.junoyi.task.service;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.task.domain.dto.TaskListDTO;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskListVO;

/**
 * 任务列表业务接口类
 *
 * @author Fan
 */
public interface ITaskListService {

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<TaskListVO> getTaskList(TaskListQueryDTO queryDTO, PageQuery pageQuery);

    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskListDetailVO getTaskDetail(Long taskId);

    /**
     * 添加任务
     * @param dto 任务DTO
     */
    void addTask(TaskListDTO dto);

    /**
     * 修改任务
     * @param dto 任务DTO
     */
    void updateTask(TaskListDTO dto);

    /**
     * 催促提醒用户完成任务
     *
     * @param taskId 任务ID
     */
    void remindUserToCompleteTask(Long taskId);
}