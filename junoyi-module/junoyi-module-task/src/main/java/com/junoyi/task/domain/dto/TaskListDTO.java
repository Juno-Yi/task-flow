package com.junoyi.task.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务列表DTO
 *
 * @author Fan
 */
@Data
public class TaskListDTO {

    /**
     * 任务ID主键（修改时传输）
     */
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
     * 任务优先级
     */
    private Integer priority;

    /**
     * 截止时间
     */
    private Date dueTime;

    /**
     * 执行人ID列表
     */
    private List<Long> userIds;

    /**
     * 项目负责人
     */
    private Long ownerUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否同步企业微信日程
     */
    private Boolean syncSchedule;
}