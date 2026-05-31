package com.junoyi.project.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目甘特图 VO
 *
 * @author Fan
 */
@Data
public class ProjectGanttVO {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 项目类型
     */
    private String projectTitle;

    /**
     * 项目状态
     */
    private Integer status;

    /**
     * 项目状态标签（字典翻译）
     */
    private String statusLabel;

    /**
     * 项目状态标签类型（字典翻译）
     */
    private String statusType;

    /**
     * 项目类型
     */
    private Integer type;

    /**
     * 项目类型标签（字典翻译）
     */
    private String typeLabel;

    /**
     * 项目类型标签类型（字典翻译）
     */
    private String typeLabelType;

    /**
     * 项目优先级
     */
    private Integer priority;

    /**
     * 项目优先级标签（字典翻译）
     */
    private String priorityLabel;

    /**
     * 项目优先级标签类型（字典翻译）
     */
    private String priorityType;

    /**
     * 项目负责人ID
     */
    private Long leader;

    /**
     * 项目负责人名称
     */
    private String leaderName;

    /**
     * 项目进度率(0~100)
     */
    private BigDecimal completionRate;

    /**
     * 是否逾期
     */
    private boolean isOverdue;

    /**
     * 项目计划开始时间
     */
    private Date planStartTime;

    /**
     * 项目计划结束时间
     */
    private Date planEndTime;
}