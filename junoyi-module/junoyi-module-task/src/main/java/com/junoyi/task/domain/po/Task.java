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
     * 主键ID
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
    private  Integer priority;

    /**
     * 任务类型（0普通任务，1项目任务）
     */
    private Integer type;

    /**
     * 项目ID（如果为空就是普通任务，不是就是项目任务）
     */
    private Long projectId;

    /**
     * 关联的业务
     */
    private Long biz_id;

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
    private Date startTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 任务创建者ID
     */
    private Long creatorId;

    /**
     * 软删除
     */
    private Boolean delFlag;

}