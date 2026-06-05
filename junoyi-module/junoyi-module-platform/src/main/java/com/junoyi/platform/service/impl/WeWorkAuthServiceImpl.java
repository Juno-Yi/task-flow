package com.junoyi.platform.service.impl;

import com.junoyi.platform.enums.PlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import org.springframework.stereotype.Service;

/**
 * 企业微信平台登录认证业务接口实现
 *
 * @author Fan
 */
@Service
public class WeWorkAuthServiceImpl implements PlatformAuthService {

    /**
     * 获取平台类型
     * @return 平台类型
     */
    @Override
    public PlatformType getPlatformType() {
        return PlatformType.WEWORK;
    }
}