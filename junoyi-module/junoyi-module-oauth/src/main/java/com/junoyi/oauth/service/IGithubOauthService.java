package com.junoyi.oauth.service;

import com.junoyi.oauth.domain.dto.GithubUserDTO;
import com.junoyi.system.domain.vo.AuthVO;

/**
 * GitHub OAuth 服务接口
 *
 */
public interface IGithubOauthService {

    /**
     * 获取 GitHub 授权 URL
     *
     * @param state 状态码（用于防止 CSRF 攻击）
     * @return 授权 URL
     */
    String getAuthorizeUrl(String state);

    /**
     * 通过授权码获取 GitHub 用户信息
     *
     * @param code 授权码
     * @return GitHub 用户信息
     */
    GithubUserDTO getGithubUser(String code);

    /**
     * GitHub 登录或注册
     *
     * @param code 授权码
     * @param state 状态码
     * @return 认证信息（包含 token）
     */
    AuthVO loginOrRegister(String code, String state);
}

