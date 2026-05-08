package com.junoyi.oauth.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.service.IOauthConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Oauth 配置控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/oauth/config")
@RequiredArgsConstructor
public class OauthConfigController extends BaseController {

    private final IOauthConfigService oauthConfigService;

    /**
     * 获取Oauth配置列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "oauth.ui.config.view"
    )
    public R<PageResult<OauthConfigVO>> getOauthConfigList(){
        return R.ok(oauthConfigService.getOauthConfigList());
    }


}