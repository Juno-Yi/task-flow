package com.junoyi.platform.service.impl;

import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 钉钉平台登录认证业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class DingTalkAuthServiceImpl {

    /**
     * 获取平台类型
     * @return 平台类型
     */
//    @Override
    public ThirdPlatformType getPlatformType() {
        return ThirdPlatformType.DINGTALK;
    }
}