package com.junoyi.project.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目关键指标数据 VO
 *
 * @author Fan
 */
@Data
public class ProjectKeyDataVO {

    /**
     * 项目完成度
     */
    private BigDecimal projectCompletion;

    /**
     * 进行中的任务
     */
    private Long ongoingTasks;

    /**
     * 待开始的需求
     */
    private Long pendingRequirements;

    /**
     * 逾期任务量
     */
    private Long overdueTasks;
}