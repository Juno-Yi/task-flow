package com.junoyi.system.api;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统菜单 API 接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysMenuApiImpl implements SysMenuApi {

    private final SysMenuMapper sysMenuMapper;

    /**
     * 设置菜单徽章文本
     * @param menuId 菜单ID
     * @param badgeText 菜单徽章文本
     */
    @Override
    public void setMenuBadgeText(Long menuId, String badgeText) {
        if (menuId == 0 || menuId == null)
            return;
        LambdaUpdateWrapper<SysMenu> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SysMenu::getId, menuId)
                .set(SysMenu::getShowBadge, true)
                .set(SysMenu::getShowTextBadge, badgeText);

        sysMenuMapper.update(lambdaUpdateWrapper);
    }
}