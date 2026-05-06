package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 权限池响应数据
 *
 * @author Fan
 */
@Data
public class SysPermissionVO {

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
