package com.junoyi.project.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 添加项目成员DTO
 *
 * @author Fan
 */
@Data
public class ProjectMemberAddDTO {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 项目角色
     */
    private String role;
}
