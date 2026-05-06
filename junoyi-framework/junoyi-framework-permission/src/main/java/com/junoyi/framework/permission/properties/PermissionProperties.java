package com.junoyi.framework.permission.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 权限配置属性类
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.permission")
public class PermissionProperties {

    /**
     * 是否启用权限控制功能（默认启用）
     */
    private boolean enable = true;

    /**
     * 是否启用字段权限功能（默认启用）
     */
    private boolean fieldPermissionEnable = true;
}
