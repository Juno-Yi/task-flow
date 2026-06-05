package com.junoyi.platform.service;


import com.junoyi.platform.enums.PlatformType;

/**
 * 平台登录认证业务接口
 *
 * @author Fan
 */
public interface PlatformAuthService {

    /**
     * 获取平台类型
     * @return 平台类型
     */
    PlatformType getPlatformType();
}
