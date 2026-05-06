package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 菜单查询参数
 *
 * @author Fan
 */
@Data
public class SysMenuQueryDTO {

    /**
     * 菜单标题（模糊查询）
     */
    private String title;

    /**
     * 菜单类型（0目录 1菜单 2按钮）
     */
    private Integer menuType;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;
}
