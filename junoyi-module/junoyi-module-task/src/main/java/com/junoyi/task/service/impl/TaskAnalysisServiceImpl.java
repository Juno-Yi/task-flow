package com.junoyi.task.service.impl;

import com.junoyi.task.domain.vo.TaskAnalysisOverviewVO;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.service.ITaskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 任务分析业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskAnalysisServiceImpl implements ITaskAnalysisService {

    private final TaskMapper taskMapper;

    /**
     * 获取任务分析总览统计数据
     * @return 任务分析总览统计数据
     */
    @Override
    public TaskAnalysisOverviewVO getTaskAnalysisOverview() {
        return null;
    }
}