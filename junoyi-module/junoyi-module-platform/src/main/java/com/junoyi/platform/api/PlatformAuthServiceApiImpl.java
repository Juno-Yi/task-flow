package com.junoyi.platform.api;

import com.junoyi.platform.domain.OauthConfig;
import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.platform.factory.PlatformAuthFactory;
import com.junoyi.platform.service.PlatformAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 平台登录认证业务API接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class PlatformAuthServiceApiImpl implements PlatformAuthServiceApi {

    private final PlatformAuthFactory platformAuthFactory;

    /**
     * 获取扫码登录地址
     * @param thirdPlatformType 平台类型
     * @param state 防止伪造
     * @return 扫码登录地址URL
     */
    @Override
    public String getQrLoginUrl(ThirdPlatformType thirdPlatformType, String state) {
        return platformAuthFactory.get(thirdPlatformType).getAuthorizeUrl(state);
    }

    /**
     * 获取第三方平台的Oauth配置
     * @param thirdPlatformType 平台类型
     * @return Oauth配置
     */
    @Override
    public OauthConfig getOauthConfig(ThirdPlatformType thirdPlatformType) {
        return platformAuthFactory.get(thirdPlatformType).getOauthConfig();
    }
}