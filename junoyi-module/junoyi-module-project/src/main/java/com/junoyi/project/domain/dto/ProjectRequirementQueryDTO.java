package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目需求查询 DTO
 *
 * @author Fan
 */
@Data
public class ProjectRequirementQueryDTO {

    /**
     * 需求标题（模糊查询）
     */
    private String title;

    /**
     * 需求优先级
     */
    private Integer priority;

    /**
     * 需求状态
     */
    private Integer status;

    /**
     * 需求来源
     */
    private Integer Type;
}