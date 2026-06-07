package com.junoyi.platform.client;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.platform.properties.WeWorkProperties;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;

/**
 * 企业微信统一客户端
 *
 * 如果platform模块业务中如果需要调用关于企业微信应用的API，
 * 都需要统一去注入获取WeWorkClient的bean实例，然后通过本类实例操作调用。
 *
 * @author Fan
 */
public class WeWorkClient {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(WeWorkClient.class);

    private static final int MAX_RETRY_TIMES = 3;

    /**
     * 企业微信配置参数
     */
    private final WeWorkProperties properties;

    /**
     * 企业微信服务
     */
    private final WxCpService wxCpService;

    public WeWorkClient(WeWorkProperties workProperties, WxCpService wxCpService){
        this.properties = workProperties;
        this.wxCpService = wxCpService;
    }

    /**
     * 获取Oauth回调地址
     * @return Oauth回调地址
     */
    public String getRedirectUrl(){
        return properties.getRedirectUrl();
    }

    /**
     * 获取企业ID
     * @return 企业ID
     */
    public String getCorpId(){
        return properties.getCorpId();
    }

    /**
     * 获取应用 AgentId
     * @return 应用 AgentId
     */
    public Integer getAgentId(){
        return properties.getAgentId();
    }

    /**
     * 获取UserInfo
     *
     * 注意：企业微信有两种授权方式：
     * 1. 网页授权（oauth2/authorize）：返回 code，使用 getUserId() 获取用户ID
     * 2. 扫码登录（qrConnect）：返回 auth_code，需要使用 getUserInfo() 而不是 getAuthUserInfo()
     *
     * @param code 授权码
     * @return UserInfo
     */
    public WxCpOauth2UserInfo getOauthUserInfo(String code) throws WxErrorException {
        log.info("企业微信授权", "开始获取用户信息, code={}", code);

        // 尝试使用 getUserInfo (适用于扫码登录 qrConnect)
        try {
            WxCpOauth2UserInfo userInfo = wxCpService.getOauth2Service().getUserInfo(code);
            if (userInfo != null && userInfo.getUserId() != null) {
                log.info("企业微信授权", "通过 getUserInfo 获取成功: userId={}", userInfo.getUserId());
                return userInfo;
            }
        } catch (Exception e) {
            log.info("企业微信授权", "getUserInfo 获取失败，尝试使用 getAuthUserInfo: {}", e.getMessage());
        }

        // 如果 getUserInfo 失败，尝试使用 getAuthUserInfo (适用于网页授权 oauth2)
        WxCpOauth2UserInfo userInfo = wxCpService.getOauth2Service().getAuthUserInfo(code);
        log.info("企业微信授权", "通过 getAuthUserInfo 获取成功: userId={}", userInfo.getUserId());
        return userInfo;
    }
}