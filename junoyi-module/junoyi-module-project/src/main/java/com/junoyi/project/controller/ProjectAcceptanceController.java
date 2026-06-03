package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectAcceptanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 项目结项控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/acceptance")
@RequiredArgsConstructor
public class ProjectAcceptanceController extends BaseController {

    private final IProjectAcceptanceService projectAcceptanceService;

    /**
     * 获取项目结项列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getProjectAcceptanceList(ProjectListQueryDTO queryDTO){
        return R.ok(projectAcceptanceService.getAcceptanceList(queryDTO,buildPage()));
    }


    /**
     * 项目验收通过
     */
    @PostMapping("/{projectId}/pass")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.acceptance.pass.button"
    )
    public R<Void> acceptancePassed(@PathVariable("projectId") Long projectId){
        projectAcceptanceService.pass(projectId);
        return R.ok();
    }

    /**
     * 项目验收驳回
     */
    @PostMapping("/{projectId}/reject")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.acceptance.reject.button"
    )
    public R<Void> acceptanceRejected(@PathVariable("projectId") Long projectId){
        projectAcceptanceService.reject(projectId);
        return R.ok();
    }
}