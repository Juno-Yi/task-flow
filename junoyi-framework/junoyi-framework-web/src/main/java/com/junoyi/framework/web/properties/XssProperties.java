package com.junoyi.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * XSS 防护配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.web.xss")
public class XssProperties {

    /**
     * 是否启用 XSS 过滤
     */
    private boolean enable = true;

    /**
     * 过滤模式: clean(清理危险内容) / escape(转义特殊字符) / reject(拒绝请求)
     */
    private XSSMode mode = XSSMode.CLEAN;

    /**
     * 排除的 URL 路径（支持 Ant 风格）
     */
    private List<String> excludeUrls = new ArrayList<>();

    /**
     * 排除的请求方法（如 GET）
     */
    private List<String> excludeMethods = new ArrayList<>();

    /**
     * 排除的 Content-Type（如 multipart/form-data）
     */
    private List<String> excludeContentTypes = new ArrayList<>();

    /**
     * 是否过滤请求参数
     */
    private boolean filterParameter = true;

    /**
     * 是否过滤请求头
     */
    private boolean filterHeader = true;

    /**
     * 是否过滤请求体
     */
    private boolean filterBody = true;

    /**
     * 过滤模式枚举
     */
    public enum XSSMode {
        /**
         * 清理模式：移除危险内容
         */
        CLEAN,
        /**
         * 转义模式：转义特殊字符
         */
        ESCAPE,
        /**
         * 拒绝模式：检测到 XSS 直接拒绝请求
         */
        REJECT
    }
}
