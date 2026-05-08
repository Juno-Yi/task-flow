package com.junoyi.oauth.controller;

import cn.hutool.core.util.IdUtil;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.oauth.service.IGithubOauthService;
import com.junoyi.system.domain.vo.AuthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Github Oauth 控制器
 *
 */
@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class GithubOauthController extends BaseController {

    private final IGithubOauthService githubOauthService;

    /**
     * 获取 GitHub 授权 URL
     *
     * @return 授权 URL
     */
    @GetMapping("/authorize")
    public R<String> authorize() {
        // 生成随机 state 用于防止 CSRF 攻击
        String state = IdUtil.fastSimpleUUID();
        String authorizeUrl = githubOauthService.getAuthorizeUrl(state);
        return R.ok("获取授权链接成功", authorizeUrl);
    }

    /**
     * GitHub OAuth 回调接口
     *
     * @param code  授权码
     * @param state 状态码
     * @return 认证信息（包含 token）
     */
    @GetMapping("/callback")
    public R<AuthVO> callback(@RequestParam("code") String code,
                               @RequestParam("state") String state) {
        AuthVO authVO = githubOauthService.loginOrRegister(code, state);
        return R.ok(authVO);
    }
}