package com.junoyi.project.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 更新项目成员角色DTO
 *
 * @author Fan
 */
@Data
public class ProjectMemberUpdateRoleDTO {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 成员ID
     */
    private Long memberId;

    /**
     * 新角色
     */
    private String role;
}
