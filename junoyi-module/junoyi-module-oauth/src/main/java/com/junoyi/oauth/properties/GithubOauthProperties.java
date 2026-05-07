package com.junoyi.oauth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "junoyi.oauth.github")
public class GithubOauthProperties {

    /**
     * GitHub OAuth 应用 Client ID
     */
    private String clientId;

    /**
     * GitHub OAuth 应用 Client Secret
     */
    private String clientSecret;

    /**
     * 授权回调地址
     */
    private String redirectUri;

    /**
     * GitHub OAuth 授权地址
     */
    private String authorizeUrl = "https://github.com/login/oauth/authorize";

    /**
     * GitHub OAuth Token 获取地址
     */
    private String accessTokenUrl = "https://github.com/login/oauth/access_token";

    /**
     * GitHub 用户信息获取地址
     */
    private String userInfoUrl = "https://api.github.com/user";
}

