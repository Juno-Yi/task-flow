package com.junoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 系统参数DTO
 *
 * @author Fan
 */
@Data
public class SysConfigDTO {

    /**
     * 参数ID（修改时必填）
     */
    @JsonProperty("id")
    private Long configId;

    /**
     * 参数键名
     */
    @JsonProperty("configKey")
    @NotBlank(message = "配置键名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9.]+$", message = "配置键名只能包含字母、数字和点")
    @Size(max = 100, message = "配置键名长度不能超过100")
    private String configKey;

    /**
     * 参数键值
     */
    @JsonProperty("configValue")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 2000, message = "配置值长度不能超过2000")
    private String configValue;

    /**
     * 参数名称
     */
    @JsonProperty("configName")
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100")
    private String configName;

    /**
     * 参数类型（text/number/boolean/json）
     */
    @Pattern(regexp = "^(text|number|boolean|json)$", message = "参数类型必须是 text、number、boolean 或 json")
    private String configType;

    /**
     * 排序
     */
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    /**
     * 系统内置（Y是 N否）
     */
    @JsonProperty("isSystem")
    private String isSystem;

    /**
     * 状态（0正常 1停用）
     */
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
