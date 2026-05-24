package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectRequirementDTO;
import com.junoyi.project.domain.dto.ProjectRequirementQueryDTO;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import com.junoyi.project.service.IProjectPermissionService;
import com.junoyi.project.service.IProjectRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 项目需求控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/requirement")
@RequiredArgsConstructor
public class ProjectRequirementController extends BaseController {

    private final IProjectRequirementService projectRequirementService;
    private final IProjectPermissionService projectPermissionService;

    /**
     * 获取项目需求列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectRequirementVO>> getRequirementList(@PathVariable("projectId") Long projectId,
            ProjectRequirementQueryDTO queryDTO){

        // 权限判断
        if (projectId == null || projectId == 0)
            return R.fail("非法请求");

        // 在进入业务层之前，先去判断一下用户是否有该项目的查看权限，防止水平越权
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectId,currentUserId))
            return R.fail("非法请求");

        return R.ok(projectRequirementService.getRequirementList(projectId,queryDTO, buildPage()));
    }

    /**
     * 添加项目需求
     */
    @PostMapping("/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> addRequirement(@PathVariable("projectId") Long projectId,
            @RequestBody ProjectRequirementDTO dto){

        // 参数验证
        if (projectId == null || projectId == 0)
            return R.fail("项目ID不能为空");

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");

        // 判断当前用户角色是否有权限添加（负责人或管理员）
        boolean isOwner = projectPermissionService.isProjectOwner(projectId, currentUserId);
        boolean isAdmin = projectPermissionService.isProjectAdmin(projectId, currentUserId);

        if (!isOwner && !isAdmin) {
            return R.fail("权限不足，只有项目负责人或管理员可以添加需求");
        }

        projectRequirementService.addRequirement(projectId,dto);
        return R.ok();
    }

    /**
     * 修改项目需求
     */
    @PutMapping("/{projectId}")
    @PlatformScope({PlatformType.ADMIN_WEB})
    public R<Void> updateRequirement(@PathVariable("projectId") Long projectId,
                                     @RequestBody ProjectRequirementDTO dto){

        // 参数验证
        if (projectId == null || projectId == 0)
            return R.fail("项目ID不能为空");

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");

        // 判断当前用户角色是否有权限添加（负责人或管理员）
        boolean isOwner = projectPermissionService.isProjectOwner(projectId, currentUserId);
        boolean isAdmin = projectPermissionService.isProjectAdmin(projectId, currentUserId);

        if (!isOwner && !isAdmin) {
            return R.fail("权限不足，只有项目负责人或管理员可以修改需求");
        }

        projectRequirementService.updateRequirement(projectId,dto);
        return R.ok();
    }

    /**
     * 删除项目需求
     */
    @DeleteMapping("/{projectId}/remove/{requirementId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> deleteRequirement(@PathVariable("projectId") Long projectId,
                                     @PathVariable("requirementId") Long requirementId){

        // 参数验证
        if (projectId == null || projectId == 0)
            return R.fail("项目ID不能为空");

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");

        boolean isOwner = projectPermissionService.isProjectOwner(projectId, currentUserId);
        boolean isAdmin = projectPermissionService.isProjectAdmin(projectId, currentUserId);

        if (!isOwner && !isAdmin) {
            return R.fail("权限不足，只有项目负责人或管理员可以删除需求");
        }

        projectRequirementService.deleteRequirement(projectId, requirementId);
        return R.ok();
    }
}