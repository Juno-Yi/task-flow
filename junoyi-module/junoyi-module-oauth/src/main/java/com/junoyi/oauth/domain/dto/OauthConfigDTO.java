package com.junoyi.oauth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * OAuth配置DTO
 *
 * @author Fan
 */
@Data
public class OauthConfigDTO {

    /**
     * 主键ID（更新时必填）
     */
    private Long id;

    /**
     * 平台
     */
    @NotBlank(message = "平台不能为空")
    private String platform;

    /**
     * 状态（0禁用，1启用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 配置 Key
     */
    private String configKey;

    /**
     * 配置 Value
     */
    private String configValue;

    /**
     * 回调URL地址
     */
    @NotBlank(message = "回调地址不能为空")
    private String redirectUrl;

    /**
     * 备注
     */
    private String remark;
}

