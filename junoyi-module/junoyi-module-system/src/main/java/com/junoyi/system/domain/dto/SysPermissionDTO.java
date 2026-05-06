package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 权限池请求数据
 *
 * @author Fan
 */
@Data
public class SysPermissionDTO {

    private Long id;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;
}
