package com.junoyi.system.service.impl;

import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysUserConverter;
import com.junoyi.system.domain.dto.ChangePasswordDTO;
import com.junoyi.system.domain.dto.UserProfileDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.service.ISysUserCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户个人中心业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysUserCenterServiceImpl implements ISysUserCenterService {


    private final SysUserMapper sysUserMapper;
    private final SysUserConverter sysUserConverter;

    @Override
    public SysUserVO getCurrentUserProfile() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return sysUserConverter.toVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserProfile(UserProfileDTO profileDTO) {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        if (profileDTO.getNickName() != null) {
            user.setNickName(profileDTO.getNickName());
        }
        if (profileDTO.getPhonenumber() != null) {
            user.setPhonenumber(profileDTO.getPhonenumber());
        }
        if (profileDTO.getEmail() != null) {
            user.setEmail(profileDTO.getEmail());
        }
        if (profileDTO.getSex() != null) {
            user.setSex(profileDTO.getSex());
        }
        if (profileDTO.getAvatar() != null) {
            user.setAvatar(profileDTO.getAvatar());
        }

        user.setUpdateBy(SecurityUtils.getUserName());
        user.setUpdateTime(DateUtils.getNowDate());
        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCurrentUserAvatar(String avatarUrl) {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setAvatar(avatarUrl);
        user.setUpdateBy(SecurityUtils.getUserName());
        user.setUpdateTime(DateUtils.getNowDate());
        sysUserMapper.updateById(user);

        return avatarUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeCurrentUserPassword(ChangePasswordDTO passwordDTO) {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtils.matches(passwordDTO.getOldPassword(), user.getSalt(), user.getPassword())) {
            throw new RuntimeException("当前密码不正确");
        }

        // 更新新密码
        PasswordUtils.EncryptResult encryptResult = PasswordUtils.encrypt(passwordDTO.getNewPassword());
        user.setPassword(encryptResult.getEncodedPassword());
        user.setSalt(encryptResult.getSalt());
        user.setPwdUpdateTime(DateUtils.getNowDate());
        user.setUpdateBy(SecurityUtils.getUserName());
        user.setUpdateTime(DateUtils.getNowDate());
        sysUserMapper.updateById(user);
    }
}