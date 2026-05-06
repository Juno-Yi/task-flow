package com.junoyi.framework.security.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 对封装类
 * 包含 AccessToken 和 RefreshToken，通过 tokenId 关联
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {

    /**
     * Token 唯一标识（关联 AccessToken 和 RefreshToken）
     */
    private String tokenId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * AccessToken 过期时间（毫秒时间戳）
     */
    private Long accessExpireTime;

    /**
     * RefreshToken 过期时间（毫秒时间戳）
     */
    private Long refreshExpireTime;
}
