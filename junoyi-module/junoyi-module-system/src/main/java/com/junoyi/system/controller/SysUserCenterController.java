package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.UserProfileDTO;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.service.ISysUserCenterService;
import com.junoyi.system.service.impl.SysUserCenterServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人中心控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/user-center")
@RequiredArgsConstructor
public class SysUserCenterController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserCenterServiceImpl.class);

    private final ISysUserCenterService userCenterService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/profile")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<SysUserVO> getProfile() {
        return R.ok(userCenterService.getCurrentUserProfile());
    }

    /**
     * 更新当前用户个人信息
     */
    @PutMapping("/profile")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> updateProfile(@Valid @RequestBody UserProfileDTO profileDTO) {
        userCenterService.updateCurrentUserProfile(profileDTO);
        return R.ok();
    }

    /**
     * 更新当前用户头像
     */
    @PutMapping("/avatar")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<String> updateAvatar(@RequestBody java.util.Map<String, String> params) {
        String avatarUrl = params.get("avatar");
        if (avatarUrl == null || avatarUrl.isEmpty())
            return R.fail("头像地址不能为空");
        String newAvatarUrl = userCenterService.updateCurrentUserAvatar(avatarUrl);
        return R.ok(newAvatarUrl);
    }
}