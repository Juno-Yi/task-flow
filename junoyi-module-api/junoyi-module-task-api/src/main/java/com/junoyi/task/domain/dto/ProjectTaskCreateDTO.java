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
     * 截止时间
     */
    private Date dueTime;

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

    /**
     * 是否同步企业微信日程
     */
    private Boolean syncSchedule;

}