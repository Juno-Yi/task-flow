package com.junoyi.framework.security.helper;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;

/**
 * Token 业务助手接口类
 * AccessToken 和 RefreshToken 逻辑关联，但生成上完全独立
 *
 * @author Fan
 */
public interface TokenHelper {

    /**
     * 创建 Token 对（AccessToken + RefreshToken）
     * 两个 Token 通过 tokenId 关联，但各自独立生成
     * 
     * @param loginUser 登录用户信息
     * @return TokenPair 包含 AccessToken 和 RefreshToken
     */
    TokenPair createTokenPair(LoginUser loginUser);

    /**
     * 解析访问令牌
     * 
     * @param accessToken 访问令牌字符串
     * @return 登录用户信息
     */
    LoginUser parseAccessToken(String accessToken);

    /**
     * 验证访问令牌
     * 
     * @param accessToken 访问令牌字符串
     * @return 验证结果
     */
    boolean validateAccessToken(String accessToken);

    /**
     * 解析刷新令牌
     * 
     * @param refreshToken 刷新令牌字符串
     * @return 登录用户信息
     */
    LoginUser parseRefreshToken(String refreshToken);

    /**
     * 验证刷新令牌
     * 
     * @param refreshToken 刷新令牌字符串
     * @return 验证结果
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * 使用刷新令牌刷新 Token 对
     * 生成新的 AccessToken 和 RefreshToken（新的 tokenId）
     * 
     * @param refreshToken 刷新令牌字符串
     * @return 新的 TokenPair
     */
    TokenPair refreshTokenPair(String refreshToken);

    /**
     * 获取 Token 中的 tokenId
     * 
     * @param token AccessToken 或 RefreshToken
     * @return tokenId
     */
    String getTokenId(String token);
}
