package com.junoyi.framework.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JunoYi配置参数类
 *
 * @author Fan
 */
@Component
@ConfigurationProperties(prefix = "junoyi")
public class JunoYiProperties {

    /**
     * JunoYi 框架版本
     */
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
