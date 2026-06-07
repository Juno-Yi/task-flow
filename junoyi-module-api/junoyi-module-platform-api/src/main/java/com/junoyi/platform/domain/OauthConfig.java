package com.junoyi.platform.domain;


import lombok.Data;

/**
 * Oauth基础配置
 *
 * @author Fan
 */
@Data
public abstract class OauthConfig {

    /**
     * 回调地址
     */
    private String redirectUrl;
}