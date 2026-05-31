package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 项目执行控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/execution")
@RequiredArgsConstructor
public class ProjectExecutionController extends BaseController {

    private final IProjectExecutionService projectActiveService;

    /**
     * 获取项目执行中列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getExecutionList(ProjectListQueryDTO queryDTO){
        return R.ok(projectActiveService.getExecutionList(queryDTO,buildPage()));
    }

    /**
     * 发起验收
     */
    @PostMapping("/initiate/acceptance/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value =  "project.ui.execution.initiate.acceptance.button"
    )
    public R<Void> initiateAcceptance(@PathVariable("projectId") Long projectId){
        projectActiveService.initiateAcceptance(projectId);
        return R.ok();
    }
}