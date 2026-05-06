package com.junoyi.framework.apidoc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * API 文档配置属性类
 * 通过 @ConfigurationProperties 注解自动绑定 application.yml 中前缀为 "junoyi.api-doc" 的配置项
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.api-doc")
public class ApiDocProperties {

    /**
     * 是否启用 API 文档
     */
    private boolean enable = true;

    /**
     * 文档标题
     */
    private String title = "JunoYi API 文档";

    /**
     * 文档描述
     */
    private String description = "JunoYi 后台管理系统 API 接口文档";

    /**
     * 文档版本
     */
    private String version = "1.0.0";

    /**
     * 服务条款 URL
     */
    private String termsOfService;

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 许可证信息
     */
    private License license = new License();

    /**
     * 外部文档
     */
    private ExternalDocs externalDocs;

    /**
     * API 分组配置
     */
    private List<Group> groups = new ArrayList<>();

    /**
     * 联系人信息
     */
    @Data
    public static class Contact {
        /**
         * 联系人名称
         */
        private String name = "JunoYi";

        /**
         * 联系人邮箱
         */
        private String email;

        /**
         * 联系人网址
         */
        private String url;
    }

    /**
     * 许可证信息
     */
    @Data
    public static class License {
        /**
         * 许可证名称
         */
        private String name = "Apache 2.0";

        /**
         * 许可证 URL
         */
        private String url = "https://www.apache.org/licenses/LICENSE-2.0";
    }

    /**
     * 外部文档
     */
    @Data
    public static class ExternalDocs {
        /**
         * 外部文档描述
         */
        private String description;

        /**
         * 外部文档 URL
         */
        private String url;
    }

    /**
     * API 分组配置
     */
    @Data
    public static class Group {
        /**
         * 分组名称
         */
        private String name;

        /**
         * 分组显示名称
         */
        private String displayName;

        /**
         * 匹配的路径（Ant 风格）
         */
        private List<String> pathsToMatch = new ArrayList<>();

        /**
         * 匹配的包路径
         */
        private List<String> packagesToScan = new ArrayList<>();
    }
}
