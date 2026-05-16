package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 项目立项控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/setup")
@RequiredArgsConstructor
public class ProjectSetupController extends BaseController {

    private final IProjectSetupService projectSetupService;

    /**
     * 获取立项项目列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getApprovalList(ProjectListQueryDTO queryDTO){
        return R.ok(projectSetupService.getApprovalList(queryDTO,buildPage()));
    }

    /**
     * 启动立项项目
     */
    @PostMapping("/start/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.setup.button.start"
    )
    public R<Void> startProject(@PathVariable Long projectId){
        Long userId = SecurityUtils.getUserId();
        if (userId == null || userId == 0L)
            return R.fail("非法请求");
        projectSetupService.startProject(projectId);
        return R.ok();
    }
}