package com.junoyi.task.service;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListVO;

/**
 * 任务管理业务接口
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
}
