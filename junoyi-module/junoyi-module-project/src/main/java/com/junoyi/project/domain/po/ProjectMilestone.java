package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 项目里程碑
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_milestone")
public class ProjectMilestone extends BaseEntity {

    /**
     * 项目里程碑主键ID
     */
    @TableId
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 里程碑名称
     */
    private String name;

    /**
     * 里程碑描述
     */
    private String description;

    /**
     * 里程碑状态
     */
    private Integer status;

    /**
     * 截止时间
     */
    private Date dueTime;


    /**
     * 结束完成时间
     */
    private Date finishTime;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 负责人
     */
    private Long ownerId;

    /**
     * 软删除
     */
    private Boolean delFlag;
}