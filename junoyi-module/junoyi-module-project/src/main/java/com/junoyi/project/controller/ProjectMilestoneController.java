package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectMilestoneDTO;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;
import com.junoyi.project.service.IProjectMilestoneService;
import com.junoyi.project.service.IProjectPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目里程碑控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/milestone")
@RequiredArgsConstructor
public class ProjectMilestoneController extends BaseController {

    private final IProjectMilestoneService projectMilestoneService;
    private final IProjectPermissionService projectPermissionService;


    /**
     * 获取项目里程碑列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectMilestoneVO>> getProjectMilestone(@PathVariable("projectId") Long projectId){
        if (projectId == null || projectId == 0)
            return R.fail("非法请求");

        // 在进入业务层之前，先去判断一下用户是否有该项目的查看权限，防止水平越权
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectId,currentUserId))
            return R.fail("非法请求");

        return R.ok(projectMilestoneService.getProjectMilestoneList(projectId));
    }

    /**
     * 添加项目里程碑
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> addProjectMilestone(@RequestBody ProjectMilestoneDTO dto){
        // 参数验证
        if (dto.getProjectId() == null || dto.getProjectId() == 0)
            return R.fail("项目ID不能为空");

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");

        // 判断当前用户角色是否有权限添加（负责人或管理员）
        boolean isOwner = projectPermissionService.isProjectOwner(dto.getProjectId(), currentUserId);
        boolean isAdmin = projectPermissionService.isProjectAdmin(dto.getProjectId(), currentUserId);

        if (!isOwner && !isAdmin)
            return R.fail("权限不足，只有项目负责人或管理员可以添加里程碑");

        projectMilestoneService.addProjectMilestone(dto);
        return R.ok();
    }

    /**
     * 修改项目里程碑
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> updateProjectMilestone(@RequestBody ProjectMilestoneDTO dto){
        // 参数验证
        if (dto.getProjectId() == null || dto.getProjectId() == 0)
            return R.fail("项目ID不能为空");

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");

        // 判断当前用户角色是否有权限添加（负责人或管理员）
        boolean isOwner = projectPermissionService.isProjectOwner(dto.getProjectId(), currentUserId);
        boolean isAdmin = projectPermissionService.isProjectAdmin(dto.getProjectId(), currentUserId);

        if (!isOwner && !isAdmin)
            return R.fail("权限不足，只有项目负责人或管理员可以修改里程碑");

        projectMilestoneService.updateProjectMilestone(dto);
        return R.ok();
    }

    /**
     * 删除项目里程碑
     */
    @DeleteMapping("{projectId}/remove/{projectMilestoneId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> deleteProjectMilestone(@PathVariable("projectId") Long projectId,
            @PathVariable("projectMilestoneId") Long projectMilestoneId){

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

        if (!isOwner && !isAdmin)
            return R.fail("权限不足，只有项目负责人或管理员可以删除里程碑");

//        projectMilestoneService.deleteProjectMilestone(projectMilestoneId);
        return R.ok();
    }

    /**
     * 完成项目里程碑
     */
    @PutMapping("/{projectId}/complete/{projectMilestoneId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> completeProjectMilestone(@PathVariable("projectId") Long projectId,
            @PathVariable("projectMilestoneId") Long projectMilestoneId){
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

        if (!isOwner && !isAdmin)
            return R.fail("权限不足，只有项目负责人或管理员可以完成里程碑");

//        projectMilestoneService.completeProjectMilestone(projectMilestoneId);
        return R.ok();
    }
}