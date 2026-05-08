package com.junoyi.oauth.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.oauth.domain.dto.GithubUserDTO;
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

@Service
@RequiredArgsConstructor
public class GithubOauthServiceImpl implements IGithubOauthService {

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

    @Override
    public String getAuthorizeUrl(String state) {
        RedisUtils.setCacheObject(STATE_CACHE_KEY + state, state, Duration.ofSeconds(STATE_EXPIRE_SECONDS));
        return authorizeUrl +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&state=" + state +
                "&scope=user:email";
    }

    @Override
    public GithubUserDTO getGithubUser(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthVO loginOrRegister(String code, String state) {
        String cachedState = RedisUtils.getCacheObject(STATE_CACHE_KEY + state);
        if (cachedState == null || !cachedState.equals(state)) {
            throw new RuntimeException("无效的 state 参数");
        }
        RedisUtils.deleteObject(STATE_CACHE_KEY + state);

        GithubUserDTO githubUser = getGithubUser(code);

        LambdaQueryWrapper<SysUserThirdAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserThirdAuth::getAuthType, "github");
        wrapper.eq(SysUserThirdAuth::getAuthKey, String.valueOf(githubUser.getId()));
        SysUserThirdAuth thirdAuth = sysUserThirdAuthMapper.selectOne(wrapper);

        SysUser user;
        if (thirdAuth != null) {
            user = sysUserMapper.selectById(thirdAuth.getUserId());
            if (user == null || user.isDelFlag()) {
                throw new RuntimeException("用户不存在或已被删除");
            }
        } else {
            user = registerNewUser(githubUser);
            thirdAuth = new SysUserThirdAuth();
            thirdAuth.setUserId(user.getUserId());
            thirdAuth.setAuthType("github");
            thirdAuth.setAuthKey(String.valueOf(githubUser.getId()));
            thirdAuth.setCreateTime(new Date());
            thirdAuth.setRemark("GitHub: " + githubUser.getLogin());
            sysUserThirdAuthMapper.insert(thirdAuth);
        }

        return generateAuthToken(user, githubUser);
    }

    private String getAccessToken(String code) {
        try {
            HttpResponse response = HttpRequest.post(accessTokenUrl)
                    .header("Accept", "application/json")
                    .form("client_id", clientId)
                    .form("client_secret", clientSecret)
                    .form("code", code)
                    .form("redirect_uri", redirectUri)
                    .execute();

            if (!response.isOk()) {
                throw new RuntimeException("获取 GitHub access_token 失败: " + response.body());
            }

            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            String accessToken = jsonObject.getStr("access_token");
            
            if (accessToken == null || accessToken.isEmpty()) {
                throw new RuntimeException("GitHub 返回的 access_token 为空");
            }

            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("获取 GitHub access_token 失败: " + e.getMessage(), e);
        }
    }

    private GithubUserDTO getUserInfo(String accessToken) {
        try {
            HttpResponse response = HttpRequest.get(userInfoUrl)
                    .header("Authorization", "token " + accessToken)
                    .header("Accept", "application/json")
                    .execute();

            if (!response.isOk()) {
                throw new RuntimeException("获取 GitHub 用户信息失败: " + response.body());
            }

            return JSONUtil.toBean(response.body(), GithubUserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("获取 GitHub 用户信息失败: " + e.getMessage(), e);
        }
    }

    private SysUser registerNewUser(GithubUserDTO githubUser) {
        SysUser user = new SysUser();
        
        String username = "github_" + githubUser.getLogin();
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        if (sysUserMapper.selectCount(wrapper) > 0) {
            username = username + "_" + IdUtil.fastSimpleUUID().substring(0, 6);
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
        return user;
    }

    private AuthVO generateAuthToken(SysUser user, GithubUserDTO githubUser) {
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

        AuthVO authVO = new AuthVO();
        authVO.setAccessToken(tokenPair.getAccessToken());
        authVO.setRefreshToken(tokenPair.getRefreshToken());

        return authVO;
    }
}
