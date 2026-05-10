package com.junoyi.oauth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 第三方登录授权 URL VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdAuthUrlVO {

    /**
     * 授权 URL
     */
    private String authUrl;

    /**
     * 第三方登录类型
     */
    private String authType;

    /**
     * 状态参数（用于防止CSRF攻击）
     */
    private String state;
}
