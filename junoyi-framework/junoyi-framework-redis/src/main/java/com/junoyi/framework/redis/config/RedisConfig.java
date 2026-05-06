package com.junoyi.framework.redis.config;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.properties.RedissonProperties;
import com.junoyi.framework.redis.handler.KeyPrefixHandler;
import lombok.RequiredArgsConstructor;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * redis 配置
 *
 * @author Fan
 */
@AutoConfiguration
@AutoConfigureAfter(name = {
    "com.baomidou.lock.spring.boot.autoconfigure.LockAutoConfiguration",
    "org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration"
})
@EnableCaching
@EnableConfigurationProperties(RedissonProperties.class)
@RequiredArgsConstructor
public class RedisConfig {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(RedisConfig.class);

    private final RedissonProperties redissonProperties;

    private final ObjectMapper objectMapper;

    /**
     * 自定义 Redisson 的自动配置逻辑。
     * <p>
     * 根据 {@link RedissonProperties} 中的配置信息，初始化 Redisson 客户端的相关参数，
     * 包括线程数、编解码器以及支持单机或集群模式的服务器连接配置。
     * </p>
     *
     * @return RedissonAutoConfigurationCustomizer 实例，用于自定义 Redisson 配置
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        log.info("Start initializing redis configuration.");
        return config -> {
            // 基础配置：线程数与编解码器
            config.setThreads(redissonProperties.getThreads())
                    .setNettyThreads(redissonProperties.getNettyThreads())
                    .setCodec(new JsonJacksonCodec(objectMapper));

            // 单机模式配置
            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                config.useSingleServer()
                        // 设置 redis key 前缀处理器
                        .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()))
                        .setTimeout(singleServerConfig.getTimeout())
                        .setClientName(singleServerConfig.getClientName())
                        .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                        .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                        .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }

            // 集群模式配置（可选）
            RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers()
                        // 设置 redis key 前缀处理器
                        .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()))
                        .setTimeout(clusterServersConfig.getTimeout())
                        .setClientName(clusterServersConfig.getClientName())
                        .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                        .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                        .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                        .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                        .setReadMode(clusterServersConfig.getReadMode())
                        .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            }

            log.info("Initialization redis configuration completed.");
        };
    }
}
