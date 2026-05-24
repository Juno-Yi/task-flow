package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 项目需求 PO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_requirement")
public class ProjectRequirement extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

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
     * 需求状态
     */
    private Integer status;

    /**
     * 需求来源
     */
    private Integer source;

    /**
     * 需求类型
     */
    private Integer type;

    /**
     * 软删除
     */
    private Boolean delFlag;
}