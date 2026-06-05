package com.junoyi.platform.api;

import com.junoyi.platform.enums.PlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 平台登录认证业务API接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class PlatformAuthServiceApiImpl implements PlatformAuthServiceApi {

    private final Map<PlatformType, PlatformAuthService> serviceMap;

}