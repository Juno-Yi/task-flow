package com.junoyi.platform.service.impl;

import com.junoyi.platform.client.WeWorkClient;
import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.platform.service.PlatformAuthService;
import lombok.RequiredArgsConstructor;
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
}