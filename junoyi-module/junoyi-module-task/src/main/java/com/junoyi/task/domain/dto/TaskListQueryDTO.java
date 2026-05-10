package com.junoyi.task.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 任务列表查询 DTO
 *
 * @author Fan
 */
@Data
public class TaskListQueryDTO {

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务优先级
     */
    private Integer priority;

    /**
     * 执行人
     */
    private Long userId;

    /**
     * 查一个时间段任务
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}