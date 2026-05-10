package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.service.ITaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 任务业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements ITaskListService {

    private final TaskListMapper taskListMapper;

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TaskListVO> getTaskList(TaskListQueryDTO queryDTO, PageQuery pageQuery) {
        IPage<TaskListVO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        IPage<TaskListVO> result = taskListMapper.selectTaskListPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
}