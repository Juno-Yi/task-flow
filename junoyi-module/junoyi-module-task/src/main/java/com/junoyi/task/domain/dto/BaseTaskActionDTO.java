package com.junoyi.task.domain.dto;

import lombok.Data;

/**
 * 任务操作基础 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class BaseTaskActionDTO {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 提交内容
     */
    private String remark;
}