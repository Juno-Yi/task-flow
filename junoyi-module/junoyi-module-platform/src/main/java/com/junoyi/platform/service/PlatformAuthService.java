package com.junoyi.platform.service;


import com.junoyi.platform.domain.OAuthUserInfo;
import com.junoyi.platform.domain.OauthConfig;
import com.junoyi.platform.enums.ThirdPlatformType;

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
    ThirdPlatformType getPlatformType();

    /**
     * 获取授权地址
     * @param state 状态防伪造
     * @return 授权地址
     */
    String getAuthorizeUrl(String state);

    /**
     * 获取该平台的Oauth配置
     * @return Oauth配置
     */
    OauthConfig getOauthConfig();

    /**
     * 获取该平台的Oauth用户唯一标识
     * @param code code授权码
     * @return OauthUserInfo
     */
    OAuthUserInfo getOauthUserInfo(String code);
}
