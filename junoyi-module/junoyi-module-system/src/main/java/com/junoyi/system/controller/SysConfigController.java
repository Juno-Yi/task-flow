package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.dto.SysConfigQueryDTO;
import com.junoyi.system.domain.vo.SysConfigVO;
import com.junoyi.system.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统参数配置控制器
 *
 * @author Fan
 */
@Tag(name = "系统参数配置")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysConfigController.class);
    private final ISysConfigService sysConfigService;

    /**
     * 获取系统参数列表（分页）
     */
    @Operation(summary = "获取系统参数列表", description = "分页查询系统参数配置")
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.get.list"})
    public R<PageResult<SysConfigVO>> getConfigList(SysConfigQueryDTO queryDTO) {
        return R.ok(sysConfigService.getConfigList(queryDTO));
    }

    /**
     * 获取系统参数详情
     */
    @Operation(summary = "获取系统参数详情", description = "根据ID获取系统参数详情")
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.get.id"})
    public R<SysConfigVO> getConfigById(@Parameter(description = "参数ID") @PathVariable("id") Long id) {
        return R.ok(sysConfigService.getConfigById(id));
    }

    /**
     * 根据参数键名获取参数值
     */
    @Operation(summary = "根据键名获取参数值", description = "根据参数键名获取参数值，支持缓存")
    @GetMapping("/key/{configKey}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.get.key"})
    public R<String> getConfigByKey(@Parameter(description = "参数键名") @PathVariable("configKey") String configKey) {
        return R.ok(sysConfigService.getConfigByKey(configKey));
    }

    /**
     * 添加系统参数
     */
    @Operation(summary = "添加系统参数", description = "新增系统参数配置")
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.add"})
    public R<Void> addConfig(@RequestBody @Valid SysConfigDTO configDTO) {
        sysConfigService.addConfig(configDTO);
        return R.ok();
    }

    /**
     * 更新系统参数
     */
    @Operation(summary = "更新系统参数", description = "修改系统参数配置")
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.update"})
    public R<Void> updateConfig(@RequestBody @Valid SysConfigDTO configDTO) {
        sysConfigService.updateConfig(configDTO);
        return R.ok();
    }

    /**
     * 删除系统参数
     */
    @Operation(summary = "删除系统参数", description = "根据ID删除系统参数（系统内置参数不可删除）")
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.delete.id"})
    public R<Void> deleteConfig(@Parameter(description = "参数ID") @PathVariable("id") Long id) {
        sysConfigService.deleteConfig(id);
        return R.ok();
    }

    /**
     * 批量删除系统参数
     */
    @Operation(summary = "批量删除系统参数", description = "批量删除系统参数（系统内置参数不可删除）")
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.delete.batch"})
    public R<Void> deleteConfigBatch(@RequestBody List<Long> ids) {
        sysConfigService.deleteConfigBatch(ids);
        return R.ok();
    }

    /**
     * 刷新系统参数缓存
     */
    @Operation(summary = "刷新参数缓存", description = "清除所有系统参数的Redis缓存")
    @PostMapping("/refresh")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.config.view", "system.api.config.refresh"})
    public R<Void> refreshCache() {
        sysConfigService.refreshCache();
        return R.ok();
    }

    /**
     * 获取系统应用配置
     */
    @Operation(summary = "获取系统应用配置", description = "获取系统应用所需的配置信息")
    @GetMapping("/app")
    public R<List<SysConfigVO>> getSystemAppConfig() {
        // 硬编码需要获取的配置键名列表
        List<String> configKeys = List.of(
                "sys.watermark.enabled",
                "sys.watermark.text",
                "sys.menu.layout.editable",
                "sys.menu.layout.default"
        );
        
        List<SysConfigVO> configs = sysConfigService.getConfigsByKeys(configKeys);
        return R.ok(configs);
    }
}
