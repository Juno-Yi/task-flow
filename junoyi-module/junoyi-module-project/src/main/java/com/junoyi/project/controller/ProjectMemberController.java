package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectMemberAddDTO;
import com.junoyi.project.domain.dto.ProjectMemberUpdateRoleDTO;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.service.IProjectMemberService;
import com.junoyi.project.service.IProjectPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目成员控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class ProjectMemberController extends BaseController {

    private final IProjectMemberService projectMemberService;
    private final IProjectPermissionService projectPermissionService;

    /**
     * 获取项目成员列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectMemberVO>> getProjectMemberList(@PathVariable("projectId") Long projectId){
        if (projectId == null || projectId == 0)
            return R.fail("非法请求");

        // 在进入业务层之前，先去判断一下用户是否有该项目的查看权限，防止水平越权
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectId,currentUserId))
            return R.fail("非法请求");

        return R.ok(projectMemberService.getMemberList(projectId));
    }


    /**
     * 添加项目成员
     */
    @PostMapping("/add")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> addMember(@RequestBody @Validated ProjectMemberAddDTO dto) {
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
            return R.fail("权限不足，只有项目负责人或管理员可以添加成员");
        }

        // 添加成员
        projectMemberService.addMember(dto);

        return R.ok();
    }

    /**
     * 更新成员角色
     */
    @PutMapping("/role")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> updateMemberRole(@Validated @RequestBody ProjectMemberUpdateRoleDTO dto) {
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
            return R.fail("权限不足，只有项目负责人或管理员可以修改成员角色");

        projectMemberService.updateMemberRole(dto);
        return R.ok();
    }

    /**
     * 移除项目成员
     */
    @DeleteMapping("/{projectId}/remove/{memberId}")
    public R<Void> removeMember(@PathVariable("projectId") Long projectId,
            @PathVariable("memberId") Long memberId) {

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
            return R.fail("权限不足，只有项目负责人或管理员可以移出成员");

        projectMemberService.removeMember(projectId,memberId);
        return R.ok();
    }
}