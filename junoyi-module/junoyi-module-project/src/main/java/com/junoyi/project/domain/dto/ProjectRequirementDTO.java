package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目需求 DTO
 *
 * @author Fan
 */
@Data
public class ProjectRequirementDTO {

    /**
     * 修改时候使用
     */
    private Long id;

    /**
     * 项目需求标题
     */
    private String title;

    /**
     * 项目需求描述
     */
    private String description;

    /**
     * 项目需求优先级
     */
    private Integer priority;

    /**
     * 项目需求来源
     */
    private Integer source;

    /**
     * 需求类型
     */
    private Integer type;

}