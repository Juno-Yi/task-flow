package com.junoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数VO
 *
 * @author Fan
 */
@Data
public class SysConfigVO {

    /**
     * 参数ID
     */
    @JsonProperty("id")
    private Long configId;

    /**
     * 参数键名
     */
    @JsonProperty("configKey")
    private String configKey;

    /**
     * 参数键值
     */
    @JsonProperty("configValue")
    private String configValue;

    /**
     * 参数名称
     */
    @JsonProperty("configName")
    private String configName;

    /**
     * 参数类型（text/number/boolean/json）
     */
    private String configType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 系统内置（Y是 N否）
     */
    @JsonProperty("isSystem")
    private String isSystem;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
