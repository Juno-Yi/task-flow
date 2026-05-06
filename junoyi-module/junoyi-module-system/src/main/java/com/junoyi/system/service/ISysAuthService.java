package com.junoyi.system.service;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.domain.vo.UserInfoVO;

/**
 * 系统验证认证业务接口类
 *
 * @author Fan
 */
public interface ISysAuthService {

    /**
     * 登录
     * @param loginBO 登录业务数据体
     * @return 返回验证响应数据
     */
    AuthVO login(LoginBO loginBO);

    /**
     * 获取用户信息接口
     * @param loginUser 用户登录信息
     * @return 返回用户响应接口
     */
    UserInfoVO getUserInfo(LoginUser loginUser);
}
