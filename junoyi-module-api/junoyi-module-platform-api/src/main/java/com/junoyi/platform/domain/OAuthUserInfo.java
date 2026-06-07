package com.junoyi.platform.domain;

import com.junoyi.platform.enums.ThirdPlatformType;
import lombok.Data;

/**
 * Oauth用户信息
 *
 * @author Fan
 */
@Data
public class OAuthUserInfo {

    /**
     * 平台类型
     */
    private ThirdPlatformType platformType;

    /**
     * 平台用户唯一标识符
     */
    private String platformUserId;
}