package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单实体
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 父菜单ID (0 表示顶级)
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
     * 菜单标题（支持 i18n key）
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
     * 激活菜单路径（详情页用）
     */
    private String activePath;

    /**
     * 是否显示徽章
     */
    private Integer showBadge;

    /**
     * 文本徽章内容（如 New）
     */
    private String showTextBadge;

    /**
     * 权限标识（用于控制菜单访问权限）
     */
    private String permission;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;

}
