package com.junoyi.system.domain.po;

import lombok.Data;

/**
 * 系统菜单权限实体类
 * 用于封装系统菜单的权限相关信息，包括菜单ID、标题、权限标识和排序等属性
 *
 * @author Fan
 */
@Data
public class SysMenuAuth {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 权限标识
     */
    private String authPermission;

    /**
     * 排序字段
     */
    private int sort;
}
