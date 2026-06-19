package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务核心KPI VO
 *
 * @author Fan
 */
@Data
public class TaskCoreKpiVO {

    /**
     * 任务完成率（百分比）
     */
    private Double completionRate;

    /**
     * 逾期任务数
     */
    private Integer overdueTaskCount;

    /**
     * 平均处理时长（天）
     */
    private Double avgProcessDays;

    /**
     * 本期新增任务数
     */
    private Integer newTaskCount;
}

