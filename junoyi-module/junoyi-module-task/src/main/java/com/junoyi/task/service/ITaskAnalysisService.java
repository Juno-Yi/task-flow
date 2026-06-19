package com.junoyi.task.service;

import com.junoyi.task.domain.vo.TaskAnalysisVO;

/**
 * 任务分析业务接口
 *
 * @author Fan
 */
public interface ITaskAnalysisService {

    /**
     * 获取任务分析综合数据
     * @return 任务分析综合数据
     */
    TaskAnalysisVO getTaskAnalysis();
}
