package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目需求状态更新 DTO
 *
 * @author Fan
 */
@Data
public class ProjectRequirementStatusUpdateDTO {

    /**
     * 需求ID
     */
    private Long id;

    /**
     * 需求状态
     */
    private Integer status;
}

