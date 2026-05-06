package com.junoyi.system.service;

import com.junoyi.system.domain.dto.ChangePasswordDTO;
import com.junoyi.system.domain.dto.UserProfileDTO;
import com.junoyi.system.domain.vo.SysUserVO;

/**
 * 用户个人中心业务接口类
 *
 * @author Fan
 */
public interface ISysUserCenterService {

    /**
     * 获取当前用户个人信息
     * @return 用户信息
     */
    SysUserVO getCurrentUserProfile();

    /**
     * 更新当前用户个人信息
     * @param profileDTO 个人信息
     */
    void updateCurrentUserProfile(UserProfileDTO profileDTO);

    /**
     * 更新当前用户头像
     * @param avatarUrl 头像URL
     * @return 新头像URL
     */
    String updateCurrentUserAvatar(String avatarUrl);

    /**
     * 修改当前用户密码
     * @param passwordDTO 密码信息
     */
    void changeCurrentUserPassword(ChangePasswordDTO passwordDTO);
}
