package com.junoyi.oauth.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.oauth.domain.vo.ThirdAuthUrlVO;
import com.junoyi.oauth.domain.vo.WeWorkConfigVO;
import com.junoyi.oauth.service.IWeWorkService;
import com.junoyi.system.domain.vo.AuthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 企业微信 Oauth 控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class WeWorkOAuthController extends BaseController {

    private final IWeWorkService weWorkService;

    /**
     * 获取企业微信授权URL
     *
     * @return 授权URL信息
     */
    @GetMapping("/authorize-url")
    public R<ThirdAuthUrlVO> getAuthorizationUrl() {
        try {
            log.info("企业微信授权", "开始获取授权URL");
            ThirdAuthUrlVO authUrlVO = weWorkService.getAuthorizationUrl();
            log.info("企业微信授权", "授权URL: {}", authUrlVO.getAuthUrl());

            if (authUrlVO == null || authUrlVO.getAuthUrl() == null || authUrlVO.getAuthUrl().isEmpty()) {
                log.error("企业微信授权", "授权URL为空");
                return R.fail("获取授权URL失败，请检查企业微信配置");
            }

            return R.ok(authUrlVO);
        } catch (Exception e) {
            log.error("企业微信授权", "获取授权URL异常: {}", e.getMessage(), e);
            return R.fail("获取授权URL失败: " + e.getMessage());
        }
    }

    /**
     * 获取企业微信登录配置
     *
     * @return 企业微信登录配置
     */
    @GetMapping("/login-config")
    public R<WeWorkConfigVO> getLoginConfig() {
        try {
            log.info("企业微信配置", "开始获取登录配置");
            WeWorkConfigVO configVO = weWorkService.getLoginConfig();
            log.info("企业微信配置", "获取登录配置成功: corpId={}, agentId={}",
                    configVO.getCorpId(), configVO.getAgentId());

            if (configVO == null || configVO.getCorpId() == null || configVO.getCorpId().isEmpty()) {
                log.error("企业微信配置", "配置信息为空");
                return R.fail("获取企业微信配置失败");
            }

            return R.ok(configVO);
        } catch (Exception e) {
            log.error("企业微信配置", "获取登录配置异常: {}", e.getMessage(), e);
            return R.fail("获取企业微信配置失败: " + e.getMessage());
        }
    }

    /**
     * 绑定企业微信账号
     *
     * @param username 系统用户名
     * @param password 系统密码
     * @param bindToken 绑定令牌（从回调接口获取）
     * @return 认证信息
     */
    @PostMapping("/bind")
    public R<AuthVO> bindAccount(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("code") String bindToken) {
        log.info("企业微信绑定", "用户{}尝试绑定企业微信账号", username);
        AuthVO authVO = weWorkService.bindAccount(username, password, bindToken);
        return R.ok(authVO);
    }
}