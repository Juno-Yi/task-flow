package com.junoyi.oauth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 企业微信登录配置VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeWorkConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 应用AgentId
     */
    private String agentId;

    /**
     * 回调地址
     */
    private String redirectUri;

    /**
     * 状态参数
     */
    private String state;
}
