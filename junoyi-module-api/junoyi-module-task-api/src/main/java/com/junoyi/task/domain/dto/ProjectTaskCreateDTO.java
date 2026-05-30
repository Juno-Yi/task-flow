package com.junoyi.task.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 项目任务创建 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class ProjectTaskCreateDTO {

    /**
     * 所属项目Id
     */
    private Long projectId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 计划开始时间
     */
    private  Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 执行人ID列表
     */
    private List<Long> userIds;

    /**
     * 任务负责人
     */
    private Long ownerUserId;

    /**
     * 任务备注
     */
    private String remark;

}