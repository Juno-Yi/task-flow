package com.junoyi.framework.apidoc.config;

import com.junoyi.framework.apidoc.properties.ApiDocProperties;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * API 文档自动配置类
 * 基于 SpringDoc OpenAPI 3
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties(ApiDocProperties.class)
@ConditionalOnProperty(prefix = "junoyi.api-doc", name = "enable", havingValue = "true", matchIfMissing = true)
public class ApiDocConfiguration {

    private static final String SECURITY_SCHEME_NAME = "Authorization";

    private final ApiDocProperties properties;

    public ApiDocConfiguration(ApiDocProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建 OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .externalDocs(buildExternalDocs())
                // 添加全局安全认证
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .schemaRequirement(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .description("JWT Token 认证，格式: Bearer {token}"));
    }

    /**
     * 构建文档基本信息
     */
    private Info buildInfo() {
        Info info = new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion());

        // 设置服务条款
        if (properties.getTermsOfService() != null) {
            info.termsOfService(properties.getTermsOfService());
        }

        // 设置联系人信息
        ApiDocProperties.Contact contactProps = properties.getContact();
        if (contactProps != null) {
            Contact contact = new Contact()
                    .name(contactProps.getName())
                    .email(contactProps.getEmail())
                    .url(contactProps.getUrl());
            info.contact(contact);
        }

        // 设置许可证信息
        ApiDocProperties.License licenseProps = properties.getLicense();
        if (licenseProps != null) {
            License license = new License()
                    .name(licenseProps.getName())
                    .url(licenseProps.getUrl());
            info.license(license);
        }

        return info;
    }

    /**
     * 构建外部文档
     */
    private ExternalDocumentation buildExternalDocs() {
        ApiDocProperties.ExternalDocs externalDocsProps = properties.getExternalDocs();
        if (externalDocsProps == null || externalDocsProps.getUrl() == null) {
            return null;
        }
        return new ExternalDocumentation()
                .description(externalDocsProps.getDescription())
                .url(externalDocsProps.getUrl());
    }

    /**
     * 创建默认分组（全部接口）
     */
    @Bean
    public GroupedOpenApi defaultGroup() {
        return GroupedOpenApi.builder()
                .group("default")
                .displayName("全部接口")
                .pathsToMatch("/**")
                .build();
    }

    /**
     * 系统管理分组
     */
    @Bean
    public GroupedOpenApi systemGroup() {
        List<ApiDocProperties.Group> groups = properties.getGroups();
        ApiDocProperties.Group systemGroup = groups.stream()
                .filter(g -> "system".equals(g.getName()))
                .findFirst()
                .orElse(null);
        
        if (systemGroup != null && systemGroup.getPathsToMatch() != null && !systemGroup.getPathsToMatch().isEmpty()) {
            return GroupedOpenApi.builder()
                    .group(systemGroup.getName())
                    .displayName(systemGroup.getDisplayName() != null ? systemGroup.getDisplayName() : "系统管理")
                    .pathsToMatch(systemGroup.getPathsToMatch().toArray(new String[0]))
                    .build();
        }
        
        return GroupedOpenApi.builder()
                .group("system")
                .displayName("系统管理")
                .pathsToMatch("/system/**", "/auth/**", "/captcha/**")
                .build();
    }

    /**
     * 示例模块分组
     */
    @Bean
    public GroupedOpenApi demoGroup() {
        List<ApiDocProperties.Group> groups = properties.getGroups();
        ApiDocProperties.Group demoGroup = groups.stream()
                .filter(g -> "demo".equals(g.getName()))
                .findFirst()
                .orElse(null);
        
        if (demoGroup != null && demoGroup.getPathsToMatch() != null && !demoGroup.getPathsToMatch().isEmpty()) {
            return GroupedOpenApi.builder()
                    .group(demoGroup.getName())
                    .displayName(demoGroup.getDisplayName() != null ? demoGroup.getDisplayName() : "示例模块")
                    .pathsToMatch(demoGroup.getPathsToMatch().toArray(new String[0]))
                    .build();
        }
        
        return GroupedOpenApi.builder()
                .group("demo")
                .displayName("示例模块")
                .pathsToMatch("/demo/**")
                .build();
    }
}
