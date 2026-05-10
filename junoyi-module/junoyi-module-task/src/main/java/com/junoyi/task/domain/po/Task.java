package com.junoyi.task.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * 任务 PO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("task")
public class Task extends BaseEntity {

    /**
     * 任务主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务优先级
     */
    private Integer priority;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 项目ID（关联）
     */
    private Long projectId;

    /**
     * 任务ID（关联）
     */
    private Long bizId;

    /**
     * 计划开始时间
     */
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 实际开始时间
     */
    private Date actualStartTime;

    /**
     * 实际结束时间
     */
    private Date actualEndTime;

    /**
     * 任务创建者Id
     */
    private Long creatorId;

    /**
     * 软删除标识
     */
    private Boolean delFlag;
}