package com.junoyi.framework.wework.config;

import com.junoyi.framework.wework.core.WeWorkClient;
import com.junoyi.framework.wework.properties.WeWorkProperties;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpRedissonConfigImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 企业微信自动装配
 *
 * @author Fan
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "junoyi.wework", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WeWorkProperties.class)
public class WeWorkAutoConfiguration {

    /**
     * 企业微信配置存储
     */
    @Bean
    @ConditionalOnMissingBean
    public WxCpConfigStorage wxCpConfigStorage(WeWorkProperties properties,
                                               ObjectProvider<RedissonClient> redissonClientProvider) {
        properties.validate();

        WxCpDefaultConfigImpl configStorage = buildConfigStorage(properties, redissonClientProvider.getIfAvailable());
        applyBasicConfig(configStorage, properties);
        return configStorage;
    }

    /**
     * 企业微信 SDK 服务实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WxCpService wxCpService(WxCpConfigStorage wxCpConfigStorage) {
        WxCpService service = new WxCpServiceImpl();
        service.setWxCpConfigStorage(wxCpConfigStorage);
        return service;
    }

    /**
     * 企业微信统一客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public WeWorkClient weWorkClient(WxCpService wxCpService, WeWorkProperties properties) {
        return new WeWorkClient(wxCpService, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private WxCpDefaultConfigImpl buildConfigStorage(WeWorkProperties properties, RedissonClient redissonClient) {
        if (properties.useRedisTokenStore()) {
            if (redissonClient == null) {
                throw new IllegalStateException("企业微信已配置 token-store-type=redis，但当前容器中不存在 RedissonClient");
            }
            return new WxCpRedissonConfigImpl(redissonClient, properties.getRedisKeyPrefix());
        }
        return new WxCpDefaultConfigImpl();
    }

    private void applyBasicConfig(WxCpDefaultConfigImpl configStorage, WeWorkProperties properties) {
        configStorage.setCorpId(properties.getCorpId());
        configStorage.setAgentId(properties.getAgentId());
        configStorage.setCorpSecret(properties.getSecret());
        configStorage.setOauth2redirectUri(properties.getRedirectUri());
    }
}

