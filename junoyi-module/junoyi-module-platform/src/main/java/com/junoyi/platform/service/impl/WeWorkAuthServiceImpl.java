package com.junoyi.platform.service.impl;

import com.junoyi.platform.client.WeWorkClient;
import com.junoyi.platform.domain.OAuthUserInfo;
import com.junoyi.platform.domain.OauthConfig;
import com.junoyi.platform.domain.WeWorkOauthConfig;
import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.springframework.stereotype.Service;

import static com.junoyi.framework.core.utils.ServletUtils.urlEncode;

/**
 * 企业微信平台登录认证业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class WeWorkAuthServiceImpl implements PlatformAuthService {

    private final WeWorkClient weWorkClient;

    /**
     * 获取平台类型
     * @return 平台类型
     */
    @Override
    public ThirdPlatformType getPlatformType() {
        return ThirdPlatformType.WEWORK;
    }

    /**
     * 获取授权地址
     * @param state 状态防伪造
     * @return 授权地址
     */
    @Override
    public String getAuthorizeUrl(String state) {
        return String.format(
                "https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=%s&agentid=%s&redirect_uri=%s&state=%s",
                weWorkClient.getCorpId(),
                weWorkClient.getAgentId(),
                urlEncode(weWorkClient.getRedirectUrl()),
                state
        );
    }


    /**
     * 获取移动端授权地址URL
     * @param state state
     * @return 授权地址URL
     */
    public String getMobileAuthorizeUrl(String state) {
        return String.format(
                "https://open.weixin.qq.com/connect/oauth2/authorize" +
                        "?appid=%s" +
                        "&redirect_uri=%s" +
                        "&response_type=code" +
                        "&scope=snsapi_base" +
                        "&agentid=%s" +
                        "&state=%s" +
                        "#wechat_redirect",
                weWorkClient.getCorpId(),
                urlEncode(weWorkClient.getRedirectUrl()),
                weWorkClient.getAgentId(),
                state
        );
    }

    /**
     * 获取该平台的Oauth配置
     * @return Oauth配置
     */
    @Override
    public OauthConfig getOauthConfig() {
        WeWorkOauthConfig weWorkOauthConfig = new WeWorkOauthConfig();
        weWorkOauthConfig.setCorpId(weWorkClient.getCorpId());
        weWorkOauthConfig.setAgentId(weWorkClient.getAgentId());
        weWorkOauthConfig.setRedirectUrl(weWorkClient.getRedirectUrl());
        return weWorkOauthConfig;
    }

    /**
     * 获取该平台的Oauth用户唯一标识
     * @param code code授权码
     * @return OauthUserInfo
     */
    @Override
    public OAuthUserInfo getOauthUserInfo(String code) {
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo();
        oAuthUserInfo.setPlatformType(ThirdPlatformType.WEWORK);
        try {
            WxCpOauth2UserInfo wxCpOauth2UserInfo = weWorkClient.getOauthUserInfo(code);
            oAuthUserInfo.setPlatformUserId(wxCpOauth2UserInfo.getUserId());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        return oAuthUserInfo;
    }
}