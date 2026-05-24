package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 项目需求 VO
 *
 * @author Fan
 */
@Data
public class ProjectRequirementVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 需求编号
     */
    private String requirementNo;

    /**
     * 需求标题
     */
    private String title;

    /**
     * 需求描述
     */
    private String description;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 优先级标签
     */
    private String priorityLabel;

    /**
     * 优先级类型（用于前端展示样式）
     */
    private String priorityType;

    /**
     * 需求状态
     */
    private Integer status;

    /**
     * 状态标签
     */
    private String statusLabel;

    /**
     * 状态类型（用于前端展示样式）
     */
    private String statusType;

    /**
     * 需求来源
     */
    private Integer source;

    /**
     * 来源标签
     */
    private String sourceLabel;

    /**
     * 来源类型（用于前端展示样式）
     */
    private String sourceType;

    /**
     * 需求类型
     */
    private Integer type;

    /**
     * 类型标签
     */
    private String typeLabel;

    /**
     * 类型类型（用于前端展示样式）
     */
    private String typeLabelType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}