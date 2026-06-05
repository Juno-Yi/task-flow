package com.junoyi.platform.factory;

import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 平台登录认证工厂
 *
 * @author Fan
 */
@Component
public class PlatformAuthFactory {

    /**
     * 不同平台对应的登录认证业务实现
     */
    private final Map<ThirdPlatformType, PlatformAuthService> authServiceMap = new EnumMap<>(ThirdPlatformType.class);

    /**
     * 构造方法，通过构造方法注入，然后循环遍历得出Map方便API类中去注入调用
     * @param authServices 登录业务
     */
    public PlatformAuthFactory(List<PlatformAuthService> authServices) {
        for (PlatformAuthService authService : authServices) {
            authServiceMap.put(
                    authService.getPlatformType(),
                    authService
            );
        }
    }

    /**
     * 通过平台来获取该平台的登录认证业务实现
     * @param thirdPlatformType 平台类型
     * @return 该平台的登录认证业务实现
     */
    public PlatformAuthService get(ThirdPlatformType thirdPlatformType) {
        return authServiceMap.get(thirdPlatformType);
    }
}