package com.junoyi.oauth.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.oauth.domain.dto.GithubUserDTO;
import com.junoyi.oauth.exception.OauthException;
import com.junoyi.oauth.service.IGithubOauthService;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserThirdAuth;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.event.UserLoginEvent;
import com.junoyi.system.helper.LoginUserBuilder;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserThirdAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

/**
 * Github 第三方登录业务实现类
 * @author Echo
 */
@Service
@RequiredArgsConstructor
public class GithubOauthServiceImpl implements IGithubOauthService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(GithubOauthServiceImpl.class);

    private final SysUserThirdAuthMapper sysUserThirdAuthMapper;
    private final SysUserMapper sysUserMapper;
    private final AuthHelper authHelper;
    private final LoginUserBuilder loginUserBuilder;

    @Value("${junoyi.oauth.github.client-id}")
    private String clientId;

    @Value("${junoyi.oauth.github.client-secret}")
    private String clientSecret;

    @Value("${junoyi.oauth.github.redirect-uri}")
    private String redirectUri;

    @Value("${junoyi.oauth.github.authorize-url:https://github.com/login/oauth/authorize}")
    private String authorizeUrl;

    @Value("${junoyi.oauth.github.access-token-url:https://github.com/login/oauth/access_token}")
    private String accessTokenUrl;

    @Value("${junoyi.oauth.github.user-info-url:https://api.github.com/user}")
    private String userInfoUrl;

    private static final String STATE_CACHE_KEY = "oauth:github:state:";
    private static final long STATE_EXPIRE_SECONDS = 600;

    /**
     * 生成GitHub OAuth授权URL
     *
     * <p>构造GitHub第三方登录的授权跳转链接，并将state参数缓存至Redis用于后续CSRF防护验证。</p>
     *
     * @param state CSRF防护状态参数，用于防止跨站请求伪造攻击
     * @return String GitHub授权URL，包含client_id、redirect_uri、state和scope等参数
     */
    @Override
    public String getAuthorizeUrl(String state) {
        RedisUtils.setCacheObject(STATE_CACHE_KEY + state, state, Duration.ofSeconds(STATE_EXPIRE_SECONDS));
        return authorizeUrl +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&state=" + state +
                "&scope=user:email";
    }
    /**
     * 通过授权码获取GitHub用户信息
     *
     * <p>该方法封装了OAuth流程的两个步骤：先使用授权码换取access_token，
     * 再使用access_token调用GitHub API获取用户详细信息。</p>
     *
     * @param code GitHub OAuth授权回调返回的临时授权码
     * @return GithubUserDTO GitHub用户信息对象，包含用户ID、登录名等基本信息
     */
    @Override
    public GithubUserDTO getGithubUser(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    /**
     * GitHub OAuth登录或注册
     *
     * <p>处理GitHub第三方登录的完整流程，包括state验证、用户信息获取、
     * 用户绑定关系查询/创建，以及认证令牌生成。</p>
     *
     * <p>优化策略：</p>
     * <ul>
     *   <li>优先使用已存储的token获取用户信息，减少API调用</li>
     *   <li>支持限流降级，老用户在限流时仍可登录</li>
     *   <li>防止重复请求，使用Redis缓存临时用户ID</li>
     * </ul>
     *
     * @param code GitHub OAuth授权回调返回的临时授权码
     * @param state CSRF防护状态参数，用于验证请求合法性
     * @return AuthVO 认证结果对象，包含accessToken和refreshToken
     * @throws RuntimeException 当state验证失败、用户不存在或API限流时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthVO loginOrRegister(String code, String state) {
        log.info("GitHub OAuth", "开始处理登录或注册, code={}, state={}", code, state);

        // 验证 state
        String cachedState = RedisUtils.getCacheObject(STATE_CACHE_KEY + state);
        if (cachedState == null || !cachedState.equals(state)) {
            log.error("GitHub OAuth", "state 验证失败, 缓存state={}, 请求state={}", cachedState, state);
            throw new RuntimeException("无效的 state 参数");
        }
        RedisUtils.deleteObject(STATE_CACHE_KEY + state);

        // 策略：先尝试使用已存储的 token 获取用户信息，失败后再用 code 换取新 token
        GithubUserDTO githubUser = null;
        String accessToken = null;
        SysUserThirdAuth existingAuth = null;
        boolean isRateLimited = false;

        // 步骤1：尝试从 Redis 缓存中获取临时的 GitHub 用户 ID（用于快速匹配）
        String tempGithubUserId = RedisUtils.getCacheObject("oauth:github:temp:" + code);

        if (StrUtil.isNotBlank(tempGithubUserId)) {
            // 如果缓存中有，说明这是重复请求，直接查询数据库
            log.info("GitHub OAuth", "从缓存中获取到 GitHub 用户ID: {}", tempGithubUserId);
            existingAuth = findByGithubId(tempGithubUserId);

            if (existingAuth != null && StrUtil.isNotBlank(existingAuth.getAccessToken())) {
                // 使用已存储的 token 获取用户信息
                log.info("GitHub OAuth", "使用已存储的 token 获取用户信息, userId={}", existingAuth.getUserId());
                try {
                    githubUser = getUserInfo(existingAuth.getAccessToken());
                    accessToken = existingAuth.getAccessToken();
                    log.info("GitHub OAuth", "使用已存储 token 成功获取用户信息");
                } catch (Exception e) {
                    log.warn("GitHub OAuth", "已存储的 token 失效，将使用 code 换取新 token: {}", e.getMessage());
                    existingAuth = null; // 标记为需要重新获取
                }
            }
        }

        // 步骤2：如果缓存中没有或 token 失效，使用 code 换取新 token
        if (githubUser == null) {
            try {
                log.info("GitHub OAuth", "使用 code 换取新的 access_token");
                accessToken = getAccessToken(code);
                githubUser = getUserInfo(accessToken);

                // 缓存 GitHub 用户 ID，有效期 5 分钟（防止重复请求）
                RedisUtils.setCacheObject("oauth:github:temp:" + code, String.valueOf(githubUser.getId()), Duration.ofMinutes(5));

                // 缓存完整的用户信息，有效期 5 分钟（用于降级策略）
                RedisUtils.setCacheObject("oauth:github:user:" + code, JSONUtil.toJsonStr(githubUser), Duration.ofMinutes(5));
            } catch (OauthException e) {
                // 触发限流，尝试降级策略
                log.error("GitHub OAuth", "触发 GitHub API 限流: {}", e.getMessage());
                isRateLimited = true;

                // 降级策略：如果是老用户，尝试使用已存储的 token
                githubUser = tryFallbackWithStoredToken(code);

                if (githubUser == null) {
                    // 无法降级，抛出友好的错误提示
                    throw new RuntimeException("GitHub API 访问受限，新用户暂时无法注册。老用户请稍后重试或联系管理员。");
                }

                // 查询已存储的 token
                existingAuth = findByGithubId(String.valueOf(githubUser.getId()));

                if (existingAuth == null) {
                    log.error("GitHub OAuth", "降级策略失败：未找到用户绑定关系, githubId={}", githubUser.getId());
                    throw new RuntimeException("用户绑定关系不存在，请重新授权");
                }

                accessToken = existingAuth.getAccessToken();
            }
        }

        // 步骤3：查询或创建用户绑定关系
        if (existingAuth == null) {
            existingAuth = findByGithubId(String.valueOf(githubUser.getId()));
        }

        SysUser user;
        if (existingAuth != null) {
            // 用户已绑定
            log.info("GitHub OAuth", "用户已绑定, githubId={}, userId={}", githubUser.getId(), existingAuth.getUserId());
            user = sysUserMapper.selectById(existingAuth.getUserId());
            if (user == null || user.isDelFlag()) {
                log.error("GitHub OAuth", "用户不存在或已被删除, userId={}", existingAuth.getUserId());
                throw new RuntimeException("用户不存在或已被删除");
            }

            // 只有当 token 是新获取的时候才更新（限流时不更新）
            if (!isRateLimited && !accessToken.equals(existingAuth.getAccessToken())) {
                existingAuth.setAccessToken(accessToken);
                existingAuth.setUpdateTime(new Date());
                sysUserThirdAuthMapper.updateById(existingAuth);
                log.info("GitHub OAuth", "已更新 access_token, userId={}", user.getUserId());
            } else {
                log.info("GitHub OAuth", "使用已存储的 token，无需更新");
            }
        } else {
            // 用户未绑定，注册新用户
            if (isRateLimited) {
                // 限流时不允许注册新用户
                throw new RuntimeException("GitHub API 访问受限，暂时无法注册新用户，请稍后再试。");
            }

            log.info("GitHub OAuth", "用户未绑定，开始注册新用户, githubId={}, login={}", githubUser.getId(), githubUser.getLogin());
            user = registerNewUser(githubUser);

            SysUserThirdAuth thirdAuth = new SysUserThirdAuth();
            thirdAuth.setUserId(user.getUserId());
            thirdAuth.setAuthType("github");
            thirdAuth.setAuthKey(String.valueOf(githubUser.getId()));
            thirdAuth.setAccessToken(accessToken);
            thirdAuth.setCreateTime(new Date());
            thirdAuth.setRemark("GitHub: " + githubUser.getLogin());
            sysUserThirdAuthMapper.insert(thirdAuth);
            log.info("GitHub OAuth", "新用户注册成功并保存 access_token, userId={}, githubId={}", user.getUserId(), githubUser.getId());
        }

        return generateAuthToken(user, githubUser);
    }

    /**
     * 降级策略：当 GitHub API 限流时，尝试使用已存储的 token 获取用户信息
     * 这样可以让老用户在限流期间仍然能够登录
     */
    private GithubUserDTO tryFallbackWithStoredToken(String code) {
        try {
            log.info("GitHub OAuth", "尝试降级策略：使用已存储的 token");

            // 从 Redis 中获取临时缓存的用户信息（如果有的话）
            String cachedUserInfo = RedisUtils.getCacheObject("oauth:github:user:" + code);
            if (StrUtil.isNotBlank(cachedUserInfo)) {
                log.info("GitHub OAuth", "从缓存中恢复用户信息");
                return JSONUtil.toBean(cachedUserInfo, GithubUserDTO.class);
            }

            log.warn("GitHub OAuth", "降级策略失败：无法找到缓存的用户信息");
            return null;
        } catch (Exception e) {
            log.error("GitHub OAuth", "降级策略异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据 GitHub 用户 ID 查询用户绑定关系
     *
     * @param githubId GitHub 用户 ID
     * @return SysUserThirdAuth 用户绑定关系，如果不存在则返回 null
     */
    private SysUserThirdAuth findByGithubId(String githubId) {
        LambdaQueryWrapper<SysUserThirdAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserThirdAuth::getAuthType, "github");
        wrapper.eq(SysUserThirdAuth::getAuthKey, githubId);
        return sysUserThirdAuthMapper.selectOne(wrapper);
    }

    /**
     * 使用授权码从GitHub OAuth服务获取访问令牌
     *
     * <p>通过GitHub OAuth 2.0流程，使用临时授权码换取access_token。</p>
     * <p>包含限流检测机制，当触发GitHub API限流时抛出OauthException。</p>
     *
     * @param code GitHub OAuth授权回调返回的临时授权码
     * @return String GitHub访问令牌，用于后续API调用
     * @throws OauthException 当触发API限流时抛出（HTTP状态码429）
     * @throws RuntimeException 当HTTP请求失败或响应异常时抛出
     */
    private String getAccessToken(String code) {
        try {
            log.info("GitHub OAuth", "开始获取 access_token, code={}", code);

            HttpResponse response = HttpRequest.post(accessTokenUrl)
                    .header("Accept", "application/json")
                    .form("client_id", clientId)
                    .form("client_secret", clientSecret)
                    .form("code", code)
                    .form("redirect_uri", redirectUri)
                    .timeout(10000) // 10秒超时
                    .execute();

            String responseBody = response.body();
            log.info("GitHub OAuth", "获取 access_token 响应状态: {}, body: {}", response.getStatus(), responseBody);

            if (!response.isOk()) {
                log.error("GitHub OAuth", "获取 access_token 失败, HTTP状态码: {}, 响应: {}", response.getStatus(), responseBody);

                // 检查是否是限流错误
                if (response.getStatus() == 403 || response.getStatus() == 429) {
                    throw new OauthException(429, "GitHub API 限流，请稍后再试", "RATE_LIMIT");
                }

                throw new RuntimeException("获取 GitHub access_token 失败，HTTP状态码: " + response.getStatus());
            }

            JSONObject jsonObject = JSONUtil.parseObj(responseBody);

            // 检查是否有错误信息
            if (jsonObject.containsKey("error")) {
                String error = jsonObject.getStr("error");
                String errorDescription = jsonObject.getStr("error_description", "未知错误");
                log.error("GitHub OAuth", "GitHub 返回错误: error={}, description={}", error, errorDescription);

                // 检查是否是限流错误
                if ("rate_limit_exceeded".equals(error) || errorDescription.contains("rate limit")) {
                    throw new OauthException(429, "GitHub API 限流: " + errorDescription, "RATE_LIMIT");
                }

                throw new RuntimeException("GitHub 返回错误: " + errorDescription);
            }

            String accessToken = jsonObject.getStr("access_token");

            if (StrUtil.isBlank(accessToken)) {
                log.error("GitHub OAuth", "access_token 为空, 响应内容: {}", responseBody);
                throw new RuntimeException("GitHub 返回的 access_token 为空");
            }

            log.info("GitHub OAuth", "成功获取 access_token");
            return accessToken;
        } catch (OauthException e) {
            // 限流异常直接抛出，由上层处理
            throw e;
        } catch (Exception e) {
            log.error("GitHub OAuth", "获取 access_token 异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取 GitHub access_token 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从GitHub API获取用户信息
     *
     * <p>使用access_token调用GitHub用户信息接口，获取当前授权用户的详细信息。</p>
     *
     * @param accessToken GitHub访问令牌，用于身份验证
     * @return GithubUserDTO GitHub用户信息对象，包含用户ID、登录名等基本信息
     * @throws RuntimeException 当HTTP请求失败或响应异常时抛出
     */
    private GithubUserDTO getUserInfo(String accessToken) {
        try {
            log.info("GitHub OAuth", "开始获取用户信息");

            HttpResponse response = HttpRequest.get(userInfoUrl)
                    .header("Authorization", "token " + accessToken)
                    .header("Accept", "application/json")
                    .timeout(10000) // 10秒超时
                    .execute();

            String responseBody = response.body();
            log.info("GitHub OAuth", "获取用户信息响应状态: {}", response.getStatus());

            if (!response.isOk()) {
                log.error("GitHub OAuth", "获取用户信息失败, HTTP状态码: {}, 响应: {}", response.getStatus(), responseBody);
                throw new RuntimeException("获取 GitHub 用户信息失败，HTTP状态码: " + response.getStatus());
            }

            GithubUserDTO userDTO = JSONUtil.toBean(responseBody, GithubUserDTO.class);
            log.info("GitHub OAuth", "成功获取用户信息: userId={}, login={}", userDTO.getId(), userDTO.getLogin());

            return userDTO;
        } catch (Exception e) {
            log.error("GitHub OAuth", "获取用户信息异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取 GitHub 用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 注册新的系统用户
     *
     * <p>基于GitHub用户信息创建新的系统用户账号，包括：</p>
     * <ul>
     *   <li>生成唯一用户名（处理用户名冲突）</li>
     *   <li>设置用户基本信息（昵称、邮箱、头像）</li>
     *   <li>生成随机密码并加密存储</li>
     *   <li>设置用户状态为正常</li>
     * </ul>
     *
     * @param githubUser GitHub用户DTO对象，包含GitHub第三方用户的基本信息
     * @return SysUser 创建成功的系统用户对象
     */
    private SysUser registerNewUser(GithubUserDTO githubUser) {
        SysUser user = new SysUser();

        String username = "github_" + githubUser.getLogin();

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        if (sysUserMapper.selectCount(wrapper) > 0) {
            username = username + "_" + IdUtil.fastSimpleUUID().substring(0, 6);
            log.info("GitHub OAuth", "用户名已存在，生成新用户名: {}", username);
        }

        user.setUserName(username);
        user.setNickName(githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin());
        user.setEmail(githubUser.getEmail());
        user.setAvatar(githubUser.getAvatarUrl());

        String randomPassword = IdUtil.fastSimpleUUID();
        String salt = PasswordUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(PasswordUtils.encrypt(randomPassword, salt));

        user.setStatus(SysUserStatus.NORMAL.getCode());
        user.setDelFlag(false);
        user.setCreateTime(new Date());
        user.setRemark("GitHub 第三方登录自动注册");

        sysUserMapper.insert(user);
        log.info("GitHub OAuth", "新用户创建成功: userId={}, username={}", user.getUserId(), username);
        return user;
    }

    /**
     * 为GitHub OAuth用户生成认证令牌
     *
     * <p>该方法负责为用户创建登录会话，包括：</p>
     * <ul>
     *   <li>构建登录用户对象</li>
     *   <li>生成访问令牌和刷新令牌</li>
     *   <li>发布用户登录事件</li>
     * </ul>
     *
     * @param user 系统用户对象，包含用户的基本信息
     * @param githubUser GitHub用户DTO对象，包含GitHub第三方用户信息
     * @return AuthVO 认证结果对象，包含accessToken和refreshToken
     */
    private AuthVO generateAuthToken(SysUser user, GithubUserDTO githubUser) {
        log.info("GitHub OAuth", "开始生成认证令牌, userId={}, username={}", user.getUserId(), user.getUserName());

        LoginUser loginUser = loginUserBuilder.build(user);

        String loginIp = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();
        PlatformType platformType = PlatformType.ADMIN_WEB;

        TokenPair tokenPair = authHelper.login(loginUser, platformType, loginIp, userAgent);

        EventBus.get().callEvent(new UserLoginEvent(
                loginUser,
                loginIp,
                tokenPair.getTokenId(),
                "github",
                userAgent
        ));

        log.info("GitHub OAuth", "认证令牌生成成功, userId={}, tokenId={}", user.getUserId(), tokenPair.getTokenId());

        AuthVO authVO = new AuthVO();
        authVO.setAccessToken(tokenPair.getAccessToken());
        authVO.setRefreshToken(tokenPair.getRefreshToken());

        return authVO;
    }
}
