package com.junoyi.system.service;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.vo.RouterItemVO;

import java.util.List;

/**
 * 系统路由服务接口
 *
 * @author Fan
 */
public interface ISysRouterService {

    /**
     * 获取用户路由
     *
     * @param loginUser 登录用户信息
     * @return 路由信息
     */
    List<RouterItemVO> getUserRouter(LoginUser loginUser);
}
