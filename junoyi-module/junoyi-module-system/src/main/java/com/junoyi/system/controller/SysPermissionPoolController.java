package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysPermissionDTO;
import com.junoyi.system.domain.dto.SysPermissionQueryDTO;
import com.junoyi.system.domain.vo.SysPermissionVO;
import com.junoyi.system.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统权限池控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/permission-pool")
@RequiredArgsConstructor
public class SysPermissionPoolController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysPermissionPoolController.class);

    private final ISysPermissionService sysPermissionService;

    /**
     * 获取权限池列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.pool.view", "system.api.permission.pool.get.list"}
    )
    public R<PageResult<SysPermissionVO>> getPermissionPoolList(SysPermissionQueryDTO queryDTO) {
        return R.ok(sysPermissionService.getPermissionList(queryDTO, buildPage()));
    }

    /**
     * 获取权限池下拉选项
     */
    @GetMapping("/options")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.pool.view", "system.api.permission.pool.get.options"}
    )
    public R<List<SysPermissionVO>> getPermissionPoolOptions() {
        return R.ok(sysPermissionService.getPermissionOptions());
    }

    /**
     * 添加权限
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permissio.pool.view", "system.api.permission.pool.add"}
    )
    public R<Void> addPermission(@RequestBody SysPermissionDTO dto) {
        sysPermissionService.addPermission(dto);
        return R.ok();
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.pool.view", "system.api.permission.pool.delete.id"}
    )
    public R<Void> deletePermission(@PathVariable("id") Long id) {
        sysPermissionService.deletePermission(id);
        return R.ok();
    }

    /**
     * 批量删除权限
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.pool.view", "system.api.permission.pool.delete.batch"}
    )
    public R<Void> deletePermissionBatch(@RequestBody List<Long> ids) {
        sysPermissionService.deletePermissionBatch(ids);
        return R.ok();
    }

    /**
     * 更新权限状态
     */
    @PutMapping("/{id}/status")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.permission.pool.view", "system.api.permission.pool.update.status"}
    )
    public R<Void> updatePermissionStatus(@PathVariable("id") Long id, @RequestBody SysPermissionDTO sysPermissionDTO) {
        sysPermissionService.updatePermissionStatus(id, sysPermissionDTO.getStatus());
        return R.ok();
    }
}
