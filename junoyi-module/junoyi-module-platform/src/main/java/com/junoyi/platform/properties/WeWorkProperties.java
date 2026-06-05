package com.junoyi.platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 企业微信配置参数
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(
        prefix = "junoyi.wework"
)
public class WeWorkProperties {

    /**
     * 是否去启用企业微信，默认是不启用
     */
    private boolean enabled = false;

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 应用 AgentId
     */
    private Integer agentId;

    /**
     * 应用Secret
     */
    private String secret;

    /**
     * Oauth回调地址
     */
    private String redirectUrl;

    /**
     * token 存储方式：memory / redis
     */
    private String tokenStoreType = "memory";

    /**
     * Redis key 前缀
     */
    private String redisKeyPrefix = "junoyi:wework:cp";

    /**
     * 是否使用 Redis 存储 accessToken/jsapiTicket
     */
    public boolean useRedisTokenStore() {
        return "redis".equalsIgnoreCase(tokenStoreType);
    }

    /**
     * 校验关键配置是否完整
     */
    public void validate() {
        if (isBlank(corpId)) {
            throw new IllegalArgumentException("企业微信 corpId 未配置");
        }
        if (agentId == null) {
            throw new IllegalArgumentException("企业微信 agentId 未配置");
        }
        if (isBlank(secret)) {
            throw new IllegalArgumentException("企业微信 secret 未配置");
        }
    }

    /**
     * 校验登录回调配置
     */
    public void validateRedirectUrl() {
        validate();
        if (isBlank(redirectUrl)) {
            throw new IllegalArgumentException("企业微信 redirectUri 未配置");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}