package com.junoyi.platform.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业微信Oauth配置
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WeWorkOauthConfig extends OauthConfig {

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 应用 AgentId
     */
    private Integer agentId;

}