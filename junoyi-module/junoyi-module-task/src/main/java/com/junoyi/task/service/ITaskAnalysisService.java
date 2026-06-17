package com.junoyi.task.service;

import com.junoyi.task.domain.vo.TaskAnalysisOverviewVO;

/**
 * 任务分析业务接口
 *
 * @author Fan
 */
public interface ITaskAnalysisService {

    /**
     * 获取任务分析总览统计数据
     * @return 任务分析总览统计数据
     */
    TaskAnalysisOverviewVO getTaskAnalysisOverview();
}
