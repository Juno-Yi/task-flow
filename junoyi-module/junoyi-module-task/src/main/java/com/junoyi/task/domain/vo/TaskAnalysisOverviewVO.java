package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务分析统计 VO
 *
 * @author Fan
 */
@Data
public class TaskAnalysisOverviewVO {

    /**
     * 当前月数据
     */
    TaskAnalysisOverviewItem monthData;

    /**
     * 当前季度数据
     */
    TaskAnalysisOverviewItem quarterData;

    /**
     * 当前年度数据
     */
    TaskAnalysisOverviewItem yearData;

    /**
     * 全部数据
     */
    TaskAnalysisOverviewItem allData;

    @Data
    public static class TaskAnalysisOverviewItem {

        /**
         * 待完成任务数量
         */
        private Integer pendingTaskCount;

        /**
         * 进行中任务数量
         */
        private Integer ongoingTaskCount;

        /**
         * 待审核任务数量
         */
        private Integer reviewTaskCount;

        /**
         * 已驳回任务数量
         */
        private Integer rejectedTaskCount;

        /**
         * 已完成任务数量
         */
        private Integer completedTaskCount;
    }
}