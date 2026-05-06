package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 权限池查询条件
 *
 * @author Fan
 */
@Data
public class SysPermissionQueryDTO {

    /**
     * 权限标识（模糊查询）
     */
    private String permission;

    /**
     * 权限描述（模糊查询）
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;
}
