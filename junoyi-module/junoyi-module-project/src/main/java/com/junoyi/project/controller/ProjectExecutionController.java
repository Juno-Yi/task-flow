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
     *
     * 这里权限不是作用在API调用上，是该API业务的权限逻辑判断上处理，所以这里不加@Permission注解去鉴权
     */
    @PostMapping("/initiate/acceptance/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> initiateAcceptance(@PathVariable("projectId") Long projectId){
        projectActiveService.initiateAcceptance(projectId);
        return R.ok();
    }

    /**
     * 暂停项目
     *
     * 这里权限不是作用在API调用上，是该API业务的权限逻辑判断上处理，所以这里不加@Permission注解去鉴权
     */
    @PostMapping("/pause/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> pauseProject(@PathVariable("projectId") Long projectId){
        projectActiveService.pauseProject(projectId);
        return R.ok();
    }

    /**
     * 取消暂停
     *
     * 这里权限不是作用在API调用上，是该API业务的权限逻辑判断上处理，所以这里不加@Permission注解去鉴权
     */
    @PostMapping("/pause/cancel/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> cancelPauseProject(@PathVariable("projectId") Long projectId){
        projectActiveService.cancelPauseProject(projectId);
        return R.ok();
    }

    /**
     * 终止
     *
     * 这个没有项目单独角色权限控制，只需要判断有没有权限才能调用，只在API接口上去判断即可，使用@Permission注解处理
     */
    @PostMapping("/stop/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.execution.stop.button"
    )
    public R<Void> stop(@PathVariable("projectId") Long projectId) {
        projectActiveService.stopProject(projectId);
        return R.ok();
    }
}