package com.junoyi.system.api;

/**
 * 系统菜单 API 对外接口
 * <li>
 *     这里API是其他模块对外能力，其他模块想要使用关于系统菜单功能，只需要调用这里对外API接口，注入bean，
 *     然后调用这里定义的方法。
 * </li>
 * @author Fan
 */
public interface SysMenuApi {

    /**
     * 设置菜单徽章文本
     * @param menuId 菜单ID
     * @param badgeText 菜单徽章文本
     */
    void setMenuBadgeText(Long menuId, String badgeText);
}
