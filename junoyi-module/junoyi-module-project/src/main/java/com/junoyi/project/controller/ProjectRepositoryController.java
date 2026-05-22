package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectRepositoryDTO;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;
import com.junoyi.project.service.IProjectPermissionService;
import com.junoyi.project.service.IProjectRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目仓库控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/repository")
@RequiredArgsConstructor
public class ProjectRepositoryController extends BaseController {

    private final IProjectRepositoryService projectRepositoryService;
    private final IProjectPermissionService projectPermissionService;

    /**
     * 根据项目ID获取仓库列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectRepositoryVO>> getRepositoryList(@PathVariable("projectId") Long projectId) {

        if (projectId == null || projectId == 0)
            return R.fail("非法请求");

        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectId, currentUserId))
            return R.fail("非法请求");

        return R.ok(projectRepositoryService.getRepositoryList(projectId));
    }

    /**
     * 添加项目仓库
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> addRepository(@RequestBody ProjectRepositoryDTO dto) {
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

        if (!isOwner && !isAdmin) {
            return R.fail("权限不足，只有项目负责人或管理员可以添加仓库");
        }

        projectRepositoryService.addRepository(dto);
        return R.ok();
    }

    /**
     * 更新项目仓库
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> updateRepository(@RequestBody ProjectRepositoryDTO dto) {
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
            return R.fail("权限不足，只有项目负责人或管理员可以修改仓库");

        projectRepositoryService.updateRepository(dto);
        return R.ok();
    }

    /**
     * 删除项目仓库
     */
    @DeleteMapping("/{projectId}/remote/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"project.ui.detail.view"})
    public R<Void> deleteRepository(@PathVariable("id") Long id) {
//        projectRepositoryService.(id);
        return R.ok();
    }

}