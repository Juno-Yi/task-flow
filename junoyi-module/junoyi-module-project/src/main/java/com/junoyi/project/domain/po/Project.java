package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 项目 PO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project")
public class Project extends BaseEntity {

    /** 项目ID  */
    @TableId
    private Long id;

    /** 项目编号 */
    private String no;

    /** 项目名称 */
    private String name;

    /** 项目描述 */
    private String description;

    /** 项目负责人 */
    private Long leader;

    /** 项目类型 */
    private Integer type;

    /** 项目状态 */
    private Integer status;

    /** 项目优先级 */
    private Integer priority;

    /** 软删除 */
    private boolean delFlag;

    /**
     * 计划开始时间
     */
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 项目开始时间
     */
    private Date startTime;

    /**
     * 项目结束时间
     */
    private Date endTime;
}