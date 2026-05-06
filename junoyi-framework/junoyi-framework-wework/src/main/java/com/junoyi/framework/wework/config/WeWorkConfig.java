package com.junoyi.framework.wework.config;

import com.junoyi.framework.wework.properties.WeWorkProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 企业微信配置类（兼容旧代码）
 * <p>
 * 新代码请优先使用 {@link WeWorkProperties} 与 {@link WeWorkAutoConfiguration}。
 * </p>
 *
 * @author Fan
 */
@ConfigurationProperties(prefix = "junoyi.wework")
public class WeWorkConfig extends WeWorkProperties {
}
