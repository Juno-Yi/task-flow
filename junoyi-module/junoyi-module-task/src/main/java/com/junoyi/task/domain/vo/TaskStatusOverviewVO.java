package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务状态总览 VO
 *
 * @author Fan
 */
@Data
public class TaskStatusOverviewVO {

    /**
     * 当前月数据
     */
    TaskStatusOverviewItem monthData;

    /**
     * 当前季度数据
     */
    TaskStatusOverviewItem quarterData;

    /**
     * 当前年度数据
     */
    TaskStatusOverviewItem yearData;

    /**
     * 全部数据
     */
    TaskStatusOverviewItem allData;

    @Data
    public static class TaskStatusOverviewItem {

        /**
         * 总任务量
         */
        private Integer totalTaskCount;

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