package com.junoyi.framework.security.helper;

import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.framework.security.module.TokenPair;

import java.util.List;

/**
 * 认证服务助手
 * 提供简化的认证 API，供业务模块使用
 *
 * @author Fan
 */
public interface AuthHelper {

    /**
     * 用户登录（推荐使用）
     * 业务模块验证密码后，调用此方法创建会话
     * 
     * @param loginUser    登录用户信息（必须包含 userId、userName）
     * @param platformType 登录平台类型（决定 Token 有效期）
     * @param loginIp      登录IP（可选，可传 null）
     * @param userAgent    用户代理（可选，可传 null）
     * @return TokenPair 包含 accessToken 和 refreshToken
     * 
     * @example
     * <pre>
     * // 1. 业务模块验证用户名密码
     * User user = userService.checkPassword(username, password);
     * 
     * // 2. 构建 LoginUser
     * LoginUser loginUser = LoginUser.builder()
     *     .userId(user.getId())
     *     .userName(user.getUsername())
     *     .nickName(user.getNickname())
     *     .permissions(user.getPermissions())
     *     .roles(user.getRoleIds())
     *     .build();
     * 
     * // 3. 调用登录（指定平台类型）
     * TokenPair tokenPair = authService.login(loginUser, PlatformType.ADMIN_WEB, loginIp, userAgent);
     * 
     * // 4. 返回给前端
     * return Result.ok(tokenPair);
     * </pre>
     */
    TokenPair login(LoginUser loginUser, PlatformType platformType, String loginIp, String userAgent);

    /**
     * 简化登录（只传平台类型）
     * 
     * @param loginUser    登录用户信息
     * @param platformType 登录平台类型
     * @return TokenPair
     */
    TokenPair login(LoginUser loginUser, PlatformType platformType);

    /**
     * 简化登录（使用默认平台 ADMIN_WEB）
     * 
     * @param loginUser 登录用户信息
     * @param loginIp   登录IP
     * @param userAgent 用户代理
     * @return TokenPair
     */
    TokenPair login(LoginUser loginUser, String loginIp, String userAgent);

    /**
     * 最简登录（使用默认平台 ADMIN_WEB，不传 IP 和 UserAgent）
     * 
     * @param loginUser 登录用户信息
     * @return TokenPair
     */
    TokenPair login(LoginUser loginUser);

    /**
     * 用户登出
     * 
     * @param token AccessToken 或 RefreshToken
     * @return true=登出成功
     */
    boolean logout(String token);

    /**
     * 用户登出（从当前上下文获取 Token）
     * 
     * @return true=登出成功
     */
    boolean logout();

    /**
     * 刷新 Token
     * 
     * @param refreshToken 刷新令牌
     * @return 新的 TokenPair
     */
    TokenPair refresh(String refreshToken);

    /**
     * 获取当前登录用户
     * 从 Token 中获取用户信息
     * 
     * @param token AccessToken
     * @return 登录用户信息
     */
    LoginUser getLoginUser(String token);

    /**
     * 获取当前会话
     * 
     * @param token AccessToken
     * @return 用户会话
     */
    UserSession getSession(String token);

    /**
     * 通过 tokenId 获取会话
     * 
     * @param tokenId Token 唯一标识
     * @return 用户会话
     */
    UserSession getSessionByTokenId(String tokenId);

    /**
     * 验证 Token 是否有效
     * 
     * @param token AccessToken
     * @return true=有效
     */
    boolean isValid(String token);

    /**
     * 更新用户权限
     * 当用户权限变更时，更新会话中的权限信息
     * 
     * @param tokenId   Token 唯一标识
     * @param loginUser 新的用户信息
     * @return true=更新成功
     */
    boolean updatePermissions(String tokenId, LoginUser loginUser);

    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<UserSession> getUserSessions(Long userId);

    /**
     * 踢出指定会话
     * 
     * @param tokenId Token 唯一标识
     * @return true=踢出成功
     */
    boolean kickOut(String tokenId);

    /**
     * 踢出用户所有会话
     * 
     * @param userId 用户ID
     * @return 踢出的会话数量
     */
    int kickOutAll(Long userId);

    /**
     * 处理登录失败事件
     * @param account 登录失败的账户名
     * @param platformType 登录的平台类型
     * @param ip 登录失败时的客户端IP地址
     * @return 如果返回true账号/IP已经锁定需要等待冷却，返回false就还能尝试登录
     */
    public boolean onLoginFail(String account,PlatformType platformType, String ip);

    /**
     * 处理登录成功事件
     * @param account 登录成功的账户名
     * @param platformType 登录成功的平台类型
     * @param ip 登录成功的ip
     */
    void onLoginSuccess(String account,PlatformType platformType, String ip);

}
