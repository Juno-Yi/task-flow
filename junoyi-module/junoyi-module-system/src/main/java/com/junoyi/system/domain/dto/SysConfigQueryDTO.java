package com.junoyi.system.domain.dto;

import com.junoyi.framework.core.domain.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数查询DTO
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigQueryDTO extends PageQuery {

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数类型（text/number/boolean/json）
     */
    private String configType;

    /**
     * 系统内置（1是 0否）
     */
    private Integer isSystem;
}
