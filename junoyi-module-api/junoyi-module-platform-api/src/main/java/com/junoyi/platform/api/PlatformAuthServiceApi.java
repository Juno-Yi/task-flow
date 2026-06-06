package com.junoyi.platform.api;

import com.junoyi.platform.domain.OauthConfig;
import com.junoyi.platform.enums.ThirdPlatformType;

/**
 * 平台登录业务API接口
 *
 * @author Fan
 */
public interface PlatformAuthServiceApi {

    /**
     * 获取扫码登录地址
     * @param thirdPlatformType 平台类型
     * @param state 防止伪造
     * @return 扫描登录地址URL
     */
    String getQrLoginUrl(ThirdPlatformType thirdPlatformType, String state);

    /**
     * 获取第三方平台的Oauth配置
     * @param thirdPlatformType 平台类型
     * @return Oauth配置
     */
    OauthConfig getOauthConfig(ThirdPlatformType thirdPlatformType);
}
