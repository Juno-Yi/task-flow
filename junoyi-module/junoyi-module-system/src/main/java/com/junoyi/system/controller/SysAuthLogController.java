package com.junoyi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysAuthLogQueryDTO;
import com.junoyi.system.domain.po.SysAuthLog;
import com.junoyi.system.domain.vo.SysAuthLogVO;
import com.junoyi.system.service.ISysAuthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统登录日志控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/auth-log")
@RequiredArgsConstructor
public class SysAuthLogController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysAuthLogController.class);

    private final ISysAuthLogService sysAuthLogService;

    /**
     * 分页查询登录日志
     */
    @GetMapping("/list")
    @Permission(value = {"system.ui.auth-log.view", "system.api.auth-log.get.list"})
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<SysAuthLogVO>> list(SysAuthLogQueryDTO queryDTO) {
        Page<SysAuthLog> page = buildPage();
        return R.ok(sysAuthLogService.getLoginLogList(queryDTO, page));
    }

    /**
     * 删除登录日志
     */
    @DeleteMapping("/{ids}")
    @Permission(value = {"system.ui.auth-log.button.delete", "system.api.auth-log.del"})
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> delete(@PathVariable("ids") Long[] ids) {
        sysAuthLogService.deleteLoginLog(ids);
        return R.ok();
    }

    /**
     * 清空登录日志
     */
    @DeleteMapping("/clear")
    @Permission(value = {"system.ui.auth-log.button.clear", "system.api.auth-log.clear"})
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> clear() {
        sysAuthLogService.clearLoginLog();
        return R.ok();
    }

}