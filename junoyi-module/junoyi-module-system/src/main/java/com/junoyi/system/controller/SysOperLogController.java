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
import com.junoyi.system.domain.dto.SysOperLogQueryDTO;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.domain.vo.SysOperLogVO;
import com.junoyi.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统操作日志控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/oper-log")
@RequiredArgsConstructor
public class SysOperLogController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysOperLogController.class);

    private final ISysOperLogService sysOperLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/list")
    @Permission(
            value = {"system.ui.oper-log.view", "system.api.oper-log.get.list"}
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<SysOperLogVO>> list(SysOperLogQueryDTO queryDTO) {
        Page<SysOperLog> page = buildPage();
        return R.ok(sysOperLogService.getOperationLogList(queryDTO, page));
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/{ids}")
    @Permission(
            value = {"system.ui.oper-log.view","system.api.oper-log.delete.batch"}
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> delete(@PathVariable("ids") Long[] ids) {
        sysOperLogService.deleteOperationLog(ids);
        return R.ok();
    }

    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    @Permission(
            value = {"system.ui.oper-log.view", "system.api.oper-log.clear"}
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> clear() {
        sysOperLogService.clearOperationLog();
        return R.ok();
    }
}