package com.junoyi.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问日志配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.web.access-log")
public class AccessLogProperties {

    /**
     * 是否启用访问日志
     */
    private boolean enable = true;

    /**
     * 慢请求阈值（毫秒），超过此值会以 WARN 级别输出
     */
    private long slowRequestThreshold = 3000;

    /**
     * 排除的路径（支持 Ant 风格）
     */
    private List<String> excludePaths = new ArrayList<>();

    /**
     * 是否记录请求参数（DEBUG 级别）
     */
    private boolean logRequestParams = true;

    /**
     * 是否记录请求头（DEBUG 级别）
     */
    private boolean logRequestHeaders = false;

}
