package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目日程甘特图查询参数 DTO
 *
 * @author Fan
 */
@Data
public class ProjectGanttQueryDTO {

    /**
     * 项目标题
     */
    private String projectTitle;

    /**
     * 项目负责人
     */
    private Long leader;
}