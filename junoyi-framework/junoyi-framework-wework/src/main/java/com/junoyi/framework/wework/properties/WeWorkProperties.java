package com.junoyi.framework.wework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.wework")
public class WeWorkProperties {

    /**
     * 是否启用企业微信能力
     */
    private boolean enable = true;

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 应用 AgentId
     */
    private Integer agentId;

    /**
     * 应用 Secret
     */
    private String secret;

    /**
     * OAuth 回调地址
     */
    private String redirectUri;

    /**
     * 默认授权作用域
     */
    private String scope = "snsapi_base";

    /**
     * token 存储方式：memory / redis
     */
    private String tokenStoreType = "memory";

    /**
     * Redis key 前缀
     */
    private String redisKeyPrefix = "junoyi:wework:cp";

    /**
     * 群机器人配置
     */
    private GroupBotProperties groupBot = new GroupBotProperties();

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
    public void validateRedirectUri() {
        validate();
        if (isBlank(redirectUri)) {
            throw new IllegalArgumentException("企业微信 redirectUri 未配置");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 企业微信群机器人配置
     */
    @Data
    public static class GroupBotProperties {

        /**
         * 群机器人 webhook 列表
         */
        private List<String> webhooks = new ArrayList<>();
    }
}

