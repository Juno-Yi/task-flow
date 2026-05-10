package com.junoyi.task.domain.dto;

import lombok.Data;

/**
 * 任务日志查询 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class TaskLogQueryDTO {

    /**
     * 任务标题
     */
    private String taskTitle;

    /**
     * 操作者Id
     */
    private Long operatorId;

    /**
     * 操作类型
     */
    private Long actionType;
}