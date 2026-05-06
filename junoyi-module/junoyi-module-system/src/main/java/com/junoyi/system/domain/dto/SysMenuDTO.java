package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 菜单数据传输对象
 *
 * @author Fan
 */
@Data
public class SysMenuDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单类型（0目录 1菜单 2按钮）
     */
    private Integer menuType;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 是否隐藏菜单（0否 1是）
     */
    private Integer isHide;

    /**
     * 是否隐藏标签页（0否 1是）
     */
    private Integer isHideTab;

    /**
     * 是否缓存（0否 1是）
     */
    private Integer keepAlive;

    /**
     * 是否iframe（0否 1是）
     */
    private Integer isIframe;

    /**
     * 外部链接地址
     */
    private String link;

    /**
     * 是否全屏页面（0否 1是）
     */
    private Integer isFullPage;

    /**
     * 是否固定标签页（0否 1是）
     */
    private Integer fixedTab;

    /**
     * 激活菜单路径
     */
    private String activePath;

    /**
     * 是否显示徽章
     */
    private Integer showBadge;

    /**
     * 文本徽章内容
     */
    private String showTextBadge;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
