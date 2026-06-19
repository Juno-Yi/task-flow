package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务分析页综合数据 VO
 *
 * @author Fan
 */
@Data
public class TaskAnalysisVO {

    /**
     * 任务状态总览数据
     */
    private TaskStatusOverviewVO statusOverview;

    /**
     * 核心KPI数据
     */
    private TaskCoreKpiVO coreKpi;
}