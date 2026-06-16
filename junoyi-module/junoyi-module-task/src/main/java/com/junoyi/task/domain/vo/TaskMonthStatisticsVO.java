package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 用户任务月统计数据 VO
 *
 * @author Fan
 */
@Data
public class TaskMonthStatisticsVO {

    /**
     * 待完成的任务量（当前月）
     */
    private Integer pendingTaskCount;

    /**
     * 完成的任务量 （当前月）
     */
    private Integer completedTaskCount;

    /**
     * 当前月任务数量
     */
    private Integer monthTaskCount;
}