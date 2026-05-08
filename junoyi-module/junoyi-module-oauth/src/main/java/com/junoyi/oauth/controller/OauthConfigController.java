package com.junoyi.oauth.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.service.IOauthConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Oauth 配置控制器
 *
 * @author Fan
 */
@Tag(name = "OAuth配置管理", description = "OAuth第三方登录配置管理接口")
@RestController
@RequestMapping("/oauth/config")
@RequiredArgsConstructor
public class OauthConfigController extends BaseController {

    private final IOauthConfigService oauthConfigService;

    /**
     * 分页查询Oauth配置列表
     */
    @Operation(summary = "分页查询OAuth配置列表", description = "分页查询所有第三方登录平台的配置信息")
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = "oauth.ui.config.view")
    public R<PageResult<OauthConfigVO>> getOauthConfigList(OauthConfigQueryDTO queryDTO) {
        PageResult<OauthConfigVO> pageResult = oauthConfigService.getOauthConfigList(queryDTO, buildPage());
        return R.ok(pageResult);
    }

    /**
     * 新增OAuth配置
     */
    @Operation(summary = "新增OAuth配置", description = "新增第三方登录平台配置")
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = "oauth.ui.config.button.add")
    public R<Void> addOauthConfig(@Valid @RequestBody OauthConfigDTO configDTO) {
        return R.ok();
    }

    /**
     * 更新OAuth配置
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = "oauth.ui.config.button.edit")
    public R<Void> updateOauthConfig(@Valid @RequestBody OauthConfigDTO configDTO) {
        return R.ok();
    }

    /**
     * 删除OAuth配置
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = "oauth.ui.config.button.delete")
    public R<Void> deleteOauthConfig(@PathVariable Long id) {
        return R.ok();
    }

    /**
     * 批量删除OAuth配置
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = "oauth.ui.config.button.delete")
    public R<Void> deleteOauthConfigBatch(@RequestBody List<Long> ids) {
        return R.ok();
    }
}