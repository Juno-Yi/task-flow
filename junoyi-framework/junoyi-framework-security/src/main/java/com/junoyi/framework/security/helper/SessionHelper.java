package com.junoyi.framework.security.helper;

import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.framework.security.module.TokenPair;

import java.util.List;

/**
 * 会话服务助手接口
 * 提供用户登录、登出、会话管理等功能
 *
 * @author Fan
 */
public interface SessionHelper {

    /**
     * 用户登录，创建会话
     * 
     * @param loginUser 登录用户信息（包含权限、角色等）
     * @param loginIp   登录IP地址
     * @param userAgent 用户代理信息
     * @return TokenPair 包含 AccessToken 和 RefreshToken
     */
    TokenPair login(LoginUser loginUser, String loginIp, String userAgent);

    /**
     * 用户登出，销毁会话
     * 
     * @param token AccessToken 或 RefreshToken
     * @return true=登出成功
     */
    boolean logout(String token);

    /**
     * 通过 Token 获取会话
     * 
     * @param token AccessToken 或 RefreshToken
     * @return 用户会话，不存在返回 null
     */
    UserSession getSession(String token);

    /**
     * 通过 tokenId 获取会话
     * 
     * @param tokenId Token 唯一标识
     * @return 用户会话，不存在返回 null
     */
    UserSession getSessionByTokenId(String tokenId);

    /**
     * 通过 Token 获取登录用户
     * 
     * @param token AccessToken 或 RefreshToken
     * @return 登录用户信息，不存在返回 null
     */
    LoginUser getLoginUser(String token);

    /**
     * 刷新 Token
     * 使用 RefreshToken 获取新的 TokenPair
     * 
     * @param refreshToken 刷新令牌
     * @return 新的 TokenPair
     */
    TokenPair refreshToken(String refreshToken);

    /**
     * 更新会话中的用户信息
     * 用于权限变更后更新会话
     * 
     * @param tokenId   Token 唯一标识
     * @param loginUser 新的用户信息
     * @return true=更新成功
     */
    boolean updateSession(String tokenId, LoginUser loginUser);

    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<UserSession> getUserSessions(Long userId);

    /**
     * 踢出用户的指定会话
     * 
     * @param tokenId Token 唯一标识
     * @return true=踢出成功
     */
    boolean kickOut(String tokenId);

    /**
     * 踢出用户的所有会话
     * 
     * @param userId 用户ID
     * @return 踢出的会话数量
     */
    int kickOutAll(Long userId);

    /**
     * 检查会话是否有效
     * 
     * @param token AccessToken 或 RefreshToken
     * @return true=有效
     */
    boolean isValid(String token);

    /**
     * 续期会话（更新最后访问时间）
     * 
     * @param tokenId Token 唯一标识
     */
    void touch(String tokenId);

    /**
     * 获取所有活跃会话
     * 
     * @return 所有会话列表
     */
    List<UserSession> getAllSessions();
}
