package com.junoyi.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.TaskListVO;

/**
 * 任务审核业务接口
 *
 * @author Fan
 */
public interface ITaskApprovalService {

    /**
     * 获取任务审核列表
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 任务审核列表
     */
    PageResult<TaskListVO> getApprovalList(TaskListQueryDTO queryDTO, Page<Task> page);
}
