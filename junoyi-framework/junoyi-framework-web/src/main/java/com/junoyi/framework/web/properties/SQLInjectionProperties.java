package com.junoyi.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 注入防护配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.web.sql-injection")
public class SQLInjectionProperties {

    /**
     * 是否启用 SQL 注入防护
     */
    private boolean enable = true;

    /**
     * 防护模式: detect(检测并拒绝) / clean(清理危险内容)
     */
    private SQLInjectionMode mode = SQLInjectionMode.DETECT;

    /**
     * 排除的 URL 路径（支持 Ant 风格）
     */
    private List<String> excludeUrls = new ArrayList<>();

    /**
     * 排除的请求方法
     */
    private List<String> excludeMethods = new ArrayList<>();

    /**
     * 排除检测的参数名（这些参数不进行 SQL 注入检测）
     * <p>
     * 适用场景：权限标识、代码片段等包含 SQL 关键词但非注入的字段
     * 示例：permCode, permissionCode, codeSnippet
     */
    private List<String> excludeParams = new ArrayList<>();

    /**
     * 排除检测的 JSON 字段路径（支持嵌套，如 data.permCode）
     */
    private List<String> excludeJsonFields = new ArrayList<>();

    /**
     * 是否检测请求参数
     */
    private boolean filterParameter = true;

    /**
     * 是否检测请求体
     */
    private boolean filterBody = true;

    /**
     * 是否检测请求头
     */
    private boolean filterHeader = false;

    /**
     * 自定义危险关键词（追加到默认列表）
     */
    private List<String> customKeywords = new ArrayList<>();

    /**
     * 防护模式枚举
     */
    public enum SQLInjectionMode {
        /**
         * 检测模式：检测到 SQL 注入直接拒绝请求
         */
        DETECT,
        /**
         * 清理模式：清理危险内容后继续处理
         */
        CLEAN
    }
}
