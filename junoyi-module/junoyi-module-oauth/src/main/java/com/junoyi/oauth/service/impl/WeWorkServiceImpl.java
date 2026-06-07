package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.exception.LoginException;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.oauth.domain.vo.ThirdAuthUrlVO;
import com.junoyi.oauth.domain.vo.WeWorkConfigVO;
import com.junoyi.oauth.enums.ThirdAuthType;
import com.junoyi.oauth.service.IWeWorkService;
import com.junoyi.platform.api.PlatformAuthServiceApi;
import com.junoyi.platform.domain.OAuthUserInfo;
import com.junoyi.platform.domain.WeWorkOauthConfig;
import com.junoyi.platform.enums.ThirdPlatformType;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserThirdAuth;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.event.UserLoginEvent;
import com.junoyi.system.exception.UserNotExistException;
import com.junoyi.system.exception.UserStatusIsDisableException;
import com.junoyi.system.exception.UserStatusIsLockedException;
import com.junoyi.system.helper.LoginUserBuilder;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserThirdAuthMapper;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 企业微信业务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class WeWorkServiceImpl implements IWeWorkService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(WeWorkServiceImpl.class);

    private final AuthHelper authHelper;
    private final SysUserMapper sysUserMapper;
    private final SysUserThirdAuthMapper sysUserThirdAuthMapper;
    private final LoginUserBuilder loginUserBuilder;
    private final PlatformAuthServiceApi platformAuthServiceApi;

    /**
     * 获取企业微信OAuth授权URL
     *
     * @return 授权URL信息
     */
    @Override
    public ThirdAuthUrlVO getAuthorizationUrl() {
        try {

            // 生成随机 state 用于防止 CSRF 攻击
            String random = UUID.randomUUID().toString().replace("-", "");
            String state = "WEWORK:" + random;
            String authUrl = platformAuthServiceApi.getQrLoginUrl(ThirdPlatformType.WEWORK,state);

            log.info("企业微信授权", "生成授权URL: {}", authUrl);

            return ThirdAuthUrlVO.builder()
                    .authUrl(authUrl)
                    .authType(ThirdAuthType.WEWORK.getCode())
                    .state(state)
                    .build();
        } catch (Exception e) {
            log.error("企业微信授权", "生成授权URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成授权URL失败: " + e.getMessage(), e);
        }
    }


    /**
     * 获取企业微信登录配置
     *
     * @return 企业微信登录配置
     */
    @Override
    public WeWorkConfigVO getLoginConfig() {
        try {
            // 生成随机 state 用于防止 CSRF 攻击
            String random = UUID.randomUUID().toString().replace("-", "");
            String state = "WEWORK:" + random;
            WeWorkOauthConfig weWorkOauthConfig = (WeWorkOauthConfig) platformAuthServiceApi.getOauthConfig(ThirdPlatformType.WEWORK);

            return WeWorkConfigVO.builder()
                    .corpId(weWorkOauthConfig.getCorpId())
                    .agentId(String.valueOf(weWorkOauthConfig.getAgentId()))
                    .redirectUri(weWorkOauthConfig.getRedirectUrl())
                    .state(state)
                    .build();
        } catch (Exception e) {
            log.error("企业微信配置", "获取登录配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取登录配置失败: " + e.getMessage(), e);
        }
    }


    /**
     * 处理企业微信OAuth回调
     *
     * @param code 授权码
     * @return 认证信息
     */
    @Override
    public AuthVO handleCallback(String code) {
        try {
            // 通过code获取用户信息
            OAuthUserInfo oauthUserInfo = platformAuthServiceApi.getOauthUserInfo(ThirdPlatformType.WEWORK, code);


            // 判断一下获取的OauthUserInfo是否是企业微信平台的
            if (oauthUserInfo.getPlatformType() != ThirdPlatformType.WEWORK)
                throw new LoginException("非法平台");

            // 获取企业微信用户唯一标识符
            String weworkUserId = oauthUserInfo.getPlatformUserId();

            log.info("测试","企业微信唯一标识符：" + weworkUserId);


            // 根据企业微信userId查找系统用户
            SysUser user = findUserByWeWorkUserId(weworkUserId);

            if (user == null) {
                // 用户未绑定，生成临时绑定令牌并缓存企业微信用户ID
                String bindToken = UUID.randomUUID().toString().replace("-", "");
                String cacheKey = "wework:bind:" + bindToken;

                // 缓存企业微信用户ID，有效期5分钟
                RedisUtils.setCacheObject(cacheKey, weworkUserId, Duration.ofMinutes(5));
                log.info("企业微信登录", "已缓存绑定令牌: cacheKey={}, weWorkUserId={}", cacheKey, weworkUserId);

                // 返回特殊的AuthVO，前端根据此状态跳转到绑定页面
                AuthVO authVO = new AuthVO();
                authVO.setNeedBind(true);
                authVO.setWeWorkUserId(weworkUserId);
                authVO.setCode(bindToken); // 使用 bindToken 而不是 OAuth code
                log.info("企业微信登录", "用户未绑定，需要跳转到绑定页面: weWorkUserId={}, bindToken={}", weworkUserId, bindToken);
                return authVO;
            }

            // 校验用户状态
            validateUser(user);

            // 获取请求信息
            String loginIp = ServletUtils.getClientIp();
            String userAgent = ServletUtils.getUserAgent();
            PlatformType platformType = PlatformType.ADMIN_WEB;

             // 构建 LoginUser
            LoginUser loginUser = loginUserBuilder.build(user);

            //调用 AuthHelper 登录
            TokenPair tokenPair = authHelper.login(loginUser, platformType, loginIp, userAgent);

            // 发布登录成功事件
            EventBus.get().callEvent(new UserLoginEvent(loginUser, loginIp, tokenPair.getTokenId(), "wework", userAgent));

            // 构建返回结果
            AuthVO authVO = new AuthVO();
            authVO.setAccessToken(tokenPair.getAccessToken());
            authVO.setRefreshToken(tokenPair.getRefreshToken());
            authVO.setNeedBind(false);

            return authVO;

        } catch (Exception e) {
            log.error("企业微信登录失败", "code={}, error={}", code, e.getMessage());
        }
        return null;
    }

    /**
     * 根据企业微信userId查找系统用户
     * 通过 sys_user_third_auth 表查询
     */
    private SysUser findUserByWeWorkUserId(String weWorkUserId) {
        // 查询第三方登录绑定记录
        LambdaQueryWrapper<SysUserThirdAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserThirdAuth::getAuthType, ThirdAuthType.WEWORK.getCode())
                .eq(SysUserThirdAuth::getAuthKey, weWorkUserId);

        SysUserThirdAuth thirdAuth = sysUserThirdAuthMapper.selectOne(wrapper);

        if (thirdAuth == null) {
            return null;
        }

        // 根据用户ID查询用户信息
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUserId, thirdAuth.getUserId())
                .eq(SysUser::isDelFlag, false);

        return sysUserMapper.selectOne(userWrapper);
    }

    /**
     * 校验用户状态
     */
    private void validateUser(SysUser user) {
        if (user.isDelFlag()) {
            throw new UserNotExistException("用户不存在或已被删除");
        }

        if (user.getStatus() == 0) {
            throw new UserStatusIsDisableException("用户已被禁用");
        }

        if (user.getStatus() == 2) {
            throw new UserStatusIsLockedException("用户已被锁定");
        }
    }

    /**
     * 获取企业微信用户信息（用于绑定前预览）
     *
     * @param code 授权码
     * @return 企业微信用户ID
     */
    @Override
    public String getWeWorkUserInfo(String code) {
//        try {
            // 通过code获取用户信息
//            WxCpOauth2UserInfo userInfo = weWorkClient.getOauth2UserInfo(code);
//            String weWorkUserId = userInfo.getUserId();

//            log.info("企业微信用户信息", "获取到用户ID: {}", weWorkUserId);
//            return weWorkUserId;
//        } catch (WxErrorException e) {
//            log.error("获取企业微信用户信息失败", "code={}, error={}", code, e.getMessage());
//            throw new RuntimeException("获取企业微信用户信息失败: " + e.getError().getErrorMsg());
//        }
        return null;
    }

    /**
     * 绑定企业微信账号
     *
     * @param username 系统用户名
     * @param password 系统密码
     * @param bindToken 绑定令牌（从回调接口获取）
     * @return 认证信息
     */
    @Override
    public AuthVO bindAccount(String username, String password, String bindToken) {
        try {
            // 1. 验证用户名和密码
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUserName, username)
                    .eq(SysUser::isDelFlag, false);

            SysUser user = sysUserMapper.selectOne(wrapper);

            if (user == null) {
                throw new UserNotExistException("用户名或密码错误");
            }

            // 验证密码（这里需要使用密码加密工具类，假设使用BCrypt）
            // TODO: 根据实际项目的密码加密方式进行验证
            // if (!passwordEncoder.matches(password, user.getPassword())) {
            //     throw new RuntimeException("用户名或密码错误");
            // }

            // 校验用户状态
            validateUser(user);

            // 2. 从缓存中获取企业微信用户ID
            String cacheKey = "wework:bind:" + bindToken;
            log.info("企业微信绑定", "尝试从缓存获取: cacheKey={}", cacheKey);

            String weWorkUserId = RedisUtils.getCacheObject(cacheKey);
            log.info("企业微信绑定", "从缓存获取结果: weWorkUserId={}", weWorkUserId);

            if (weWorkUserId == null || weWorkUserId.isEmpty()) {
                log.error("企业微信绑定", "绑定令牌已失效或不存在: bindToken={}, cacheKey={}", bindToken, cacheKey);
                throw new RuntimeException("绑定令牌已失效，请重新扫码");
            }

            // 删除缓存
            RedisUtils.deleteObject(cacheKey);
            log.info("企业微信绑定", "已删除缓存: cacheKey={}", cacheKey);

            log.info("企业微信绑定", "用户{}准备绑定企业微信账号: {}", username, weWorkUserId);

            // 3. 检查该企业微信账号是否已被其他用户绑定
            LambdaQueryWrapper<SysUserThirdAuth> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(SysUserThirdAuth::getAuthType, ThirdAuthType.WEWORK.getCode())
                    .eq(SysUserThirdAuth::getAuthKey, weWorkUserId);

            SysUserThirdAuth existingAuth = sysUserThirdAuthMapper.selectOne(checkWrapper);

            if (existingAuth != null && !existingAuth.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("该企业微信账号已被其他用户绑定");
            }

            // 4. 创建或更新绑定关系
            if (existingAuth == null) {
                SysUserThirdAuth thirdAuth = new SysUserThirdAuth();
                thirdAuth.setUserId(user.getUserId());
                thirdAuth.setAuthType(ThirdAuthType.WEWORK.getCode());
                thirdAuth.setAuthKey(weWorkUserId);
                sysUserThirdAuthMapper.insert(thirdAuth);
                log.info("企业微信绑定", "创建绑定关系成功: userId={}, weWorkUserId={}", user.getUserId(), weWorkUserId);
            } else {
                log.info("企业微信绑定", "绑定关系已存在: userId={}, weWorkUserId={}", user.getUserId(), weWorkUserId);
            }

            // 5. 执行登录流程
            String loginIp = ServletUtils.getClientIp();
            String userAgent = ServletUtils.getUserAgent();
            PlatformType platformType = PlatformType.ADMIN_WEB;

            LoginUser loginUser = loginUserBuilder.build(user);
            TokenPair tokenPair = authHelper.login(loginUser, platformType, loginIp, userAgent);

            // 发布登录成功事件
            EventBus.get().callEvent(new UserLoginEvent(loginUser, loginIp, tokenPair.getTokenId(), "wework_bind", userAgent));

            // 构建返回结果
            AuthVO authVO = new AuthVO();
            authVO.setAccessToken(tokenPair.getAccessToken());
            authVO.setRefreshToken(tokenPair.getRefreshToken());

            return authVO;

        } catch (UserNotExistException e) {
            log.error("企业微信绑定失败", "username={}, error={}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("企业微信绑定失败", "username={}, error={}", username, e.getMessage());
            throw new RuntimeException("企业微信绑定失败: " + e.getMessage());
        }
    }
}