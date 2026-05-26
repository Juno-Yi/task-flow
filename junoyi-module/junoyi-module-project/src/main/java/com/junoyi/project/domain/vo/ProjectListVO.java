package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 项目列表 VO 数据实体对象
 *
 * @author Fan
 */
@Data
public class ProjectListVO {

    /**
     * 项目 ID
     */
    private Long id;

    /**
     * 项目编号
     */
    private String no;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目介绍
     */
    private String description;

    /**
     * 项目负责人ID
     */
    private Long leader;

    /**
     * 项目负责人名称
     */
    private String leaderName;

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
     * 项目成员数量
     */
    private Integer memberCount;


    /** 项目进度百分比（如果任务没有，使用默认值 */
    private Integer progress = 0;

    /** 总任务数量（如果任务没有，使用默认值 */
    private Integer totalTasks = 0;

    /** 完成任务数量（如果任务没有，使用默认值）*/
    private Integer completedTasks = 0;


    /**
     * 计划开始时间
     */
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;



    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

}