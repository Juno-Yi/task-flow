package com.junoyi.oauth.domain.dto;

import com.junoyi.platform.enums.ThirdPlatformType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * OAuth 绑定缓存数据传输对象
 * 用于在用户绑定流程中临时存储第三方平台信息
 *
 * @author Fan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthBindCacheDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 第三方平台类型
     */
    private ThirdPlatformType platformType;

    /**
     * 第三方平台用户唯一标识符
     */
    private String platformUserId;
}

