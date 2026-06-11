package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.exception.LoginException;
import com.junoyi.framework.security.exception.LoginPasswordWrongException;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.oauth.exception.OauthAccountAlreadyBoundException;
import com.junoyi.oauth.exception.OauthBindFailedException;
import com.junoyi.oauth.exception.OauthBindTokenExpiredException;
import com.junoyi.oauth.exception.OauthPlatformTypeMismatchException;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.oauth.domain.dto.BindOauthParamsDTO;
import com.junoyi.oauth.domain.dto.OauthBindCacheDTO;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
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
            String authUrl = platformAuthServiceApi.getAuthorizeUrl(ThirdPlatformType.WEWORK,state);

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

            // 根据企业微信userId查找系统用户
            SysUser user = findUserByWeWorkUserId(weworkUserId);

            if (user == null) {
                // 用户未绑定，生成临时绑定令牌并缓存企业微信用户信息
                String bindToken = UUID.randomUUID().toString().replace("-", "");
                String cacheKey = "oauth:bind:" + bindToken;

                // 创建绑定缓存数据对象，包含平台类型和用户ID
                OauthBindCacheDTO bindCacheDTO = new OauthBindCacheDTO();
                bindCacheDTO.setPlatformType(ThirdPlatformType.WEWORK);
                bindCacheDTO.setPlatformUserId(weworkUserId);

                // 缓存绑定信息，有效期5分钟
                RedisUtils.setCacheObject(cacheKey, bindCacheDTO, Duration.ofMinutes(5));
                log.info("企业微信登录", "已缓存绑定令牌: cacheKey={}, platformType={}, platformUserId={}",
                        cacheKey, bindCacheDTO.getPlatformType(), bindCacheDTO.getPlatformUserId());

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
     * 绑定企业微信账号
     * @param dto 绑定并登录数据
     * @return 认证信息
     */
    @Override
    public AuthVO bindAccount(BindOauthParamsDTO dto) {
        // 验证用户并获取用户信息
        SysUser user = validateAndGetUser(dto);

        // 从缓存中获取OAuth绑定信息
        OauthBindCacheDTO bindCacheDTO = getBindCacheDTO(dto.getCode());

        // 验证平台类型
        validatePlatformType(bindCacheDTO);

        String weWorkUserId = bindCacheDTO.getPlatformUserId();

        // 删除缓存
        String cacheKey = "oauth:bind:" + dto.getCode();
        RedisUtils.deleteObject(cacheKey);
        log.info("OAuth绑定", "已删除缓存: cacheKey={}", cacheKey);

        log.info("企业微信绑定", "用户{}准备绑定企业微信账号: platformType={}, platformUserId={}",
                dto.getUsername(), bindCacheDTO.getPlatformType(), weWorkUserId);

        // 检查并创建绑定关系
        checkAndBindAccount(user.getUserId(), weWorkUserId);

        // 执行登录流程
        return performLogin(user);
    }

    /**
     * 验证用户并获取用户信息
     * @param dto 绑定参数
     * @return 用户信息
     */
    private SysUser validateAndGetUser(BindOauthParamsDTO dto) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, dto.getUsername())
                .eq(SysUser::isDelFlag, false);

        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new UserNotExistException("用户名或密码错误");
        }

        // 校验用户状态
        validateUser(user);

        // 验证密码
        if (!PasswordUtils.matches(dto.getPassword(), user.getSalt(), user.getPassword())) {
            throw new LoginPasswordWrongException("用户名或密码错误");
        }

        return user;
    }

    /**
     * 从缓存中获取OAuth绑定信息
     * @param code 绑定码
     * @return 绑定缓存信息
     */
    private OauthBindCacheDTO getBindCacheDTO(String code) {
        String cacheKey = "oauth:bind:" + code;
        log.info("OAuth绑定", "尝试从缓存获取: cacheKey={}", cacheKey);

        OauthBindCacheDTO bindCacheDTO = RedisUtils.getCacheObject(cacheKey);
        log.info("OAuth绑定", "从缓存获取结果: {}", bindCacheDTO);

        if (bindCacheDTO == null || bindCacheDTO.getPlatformUserId() == null) {
            log.error("OAuth绑定", "绑定令牌已失效或不存在: bindToken={}, cacheKey={}", code, cacheKey);
            throw new OauthBindTokenExpiredException("绑定令牌已失效，请重新扫码");
        }

        return bindCacheDTO;
    }

    /**
     * 验证平台类型
     * @param bindCacheDTO 绑定缓存信息
     */
    private void validatePlatformType(OauthBindCacheDTO bindCacheDTO) {
        if (bindCacheDTO.getPlatformType() != ThirdPlatformType.WEWORK) {
            log.error("OAuth绑定", "平台类型不匹配: expected=WEWORK, actual={}", bindCacheDTO.getPlatformType());
            throw new OauthPlatformTypeMismatchException("平台类型不匹配");
        }
    }

    /**
     * 检查并绑定账号
     * @param userId 用户ID
     * @param weWorkUserId 企业微信用户ID
     */
    private void checkAndBindAccount(Long userId, String weWorkUserId) {
        // 检查该系统用户是否已绑定企业微信账号
        LambdaQueryWrapper<SysUserThirdAuth> userBindingWrapper = new LambdaQueryWrapper<>();
        userBindingWrapper.eq(SysUserThirdAuth::getUserId, userId)
                .eq(SysUserThirdAuth::getAuthType, ThirdAuthType.WEWORK.getCode());

        SysUserThirdAuth userBinding = sysUserThirdAuthMapper.selectOne(userBindingWrapper);

        if (userBinding != null) {
            // 如果已绑定的是同一个企业微信账号，直接返回
            if (userBinding.getAuthKey().equals(weWorkUserId)) {
                log.info("企业微信绑定", "绑定关系已存在: userId={}, weWorkUserId={}", userId, weWorkUserId);
                return;
            }
            // 如果已绑定了不同的企业微信账号，抛出异常
            throw new OauthAccountAlreadyBoundException("该系统账号已绑定其他企业微信账号");
        }

        // 检查该企业微信账号是否已被其他用户绑定
        LambdaQueryWrapper<SysUserThirdAuth> weWorkBindingWrapper = new LambdaQueryWrapper<>();
        weWorkBindingWrapper.eq(SysUserThirdAuth::getAuthType, ThirdAuthType.WEWORK.getCode())
                .eq(SysUserThirdAuth::getAuthKey, weWorkUserId);

        SysUserThirdAuth weWorkBinding = sysUserThirdAuthMapper.selectOne(weWorkBindingWrapper);

        if (weWorkBinding != null && !weWorkBinding.getUserId().equals(userId)) {
            throw new OauthAccountAlreadyBoundException("该企业微信账号已被其他用户绑定");
        }

        // 创建绑定关系
        SysUserThirdAuth thirdAuth = new SysUserThirdAuth();
        thirdAuth.setUserId(userId);
        thirdAuth.setAuthType(ThirdAuthType.WEWORK.getCode());
        thirdAuth.setAuthKey(weWorkUserId);
        thirdAuth.setCreateTime(new Date());
        thirdAuth.setCreateBy("system");
        thirdAuth.setUpdateTime(new Date());
        thirdAuth.setUpdateBy("system");
        // 如果有登录用户信息，设置创建者和更新者
        // thirdAuth.setCreateBy(SecurityUtils.getUsername());
        // thirdAuth.setUpdateBy(SecurityUtils.getUsername());

        sysUserThirdAuthMapper.insert(thirdAuth);
        log.info("企业微信绑定", "创建绑定关系成功: userId={}, weWorkUserId={}", userId, weWorkUserId);
    }

    /**
     * 执行登录流程
     * @param user 用户信息
     * @return 认证信息
     */
    private AuthVO performLogin(SysUser user) {
        try {
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
        } catch (LoginException e) {
            log.error("企业微信绑定", "登录失败: userId={}, error={}", user.getUserId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("企业微信绑定", "登录失败: userId={}, error={}", user.getUserId(), e.getMessage());
            throw new OauthBindFailedException("企业微信绑定后登录失败", e);
        }
    }
}