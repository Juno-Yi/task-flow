package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 系统角色传输数据
 *
 * @author Fan
 */
@Data
public class SysRoleDTO {

    /**
     * 角色ID（修改时必填）
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 数据权限范围
     */
    private String dataScope;

    /**
     * 角色状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;
}
