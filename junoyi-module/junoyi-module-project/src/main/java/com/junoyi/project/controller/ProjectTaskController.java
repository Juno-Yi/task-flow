package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.service.IProjectPermissionService;
import com.junoyi.project.service.IProjectTaskService;
import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目任务控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/task")
@RequiredArgsConstructor
public class ProjectTaskController extends BaseController {

    private final IProjectTaskService projectTaskService;
    private final IProjectPermissionService projectPermissionService;

    /**
     * 获取项目的任务列表
     */
    @GetMapping("/list/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectTaskItemVO>> getProjectTaskList(@PathVariable("id") Long id){
        if (id == null || id == 0)
            return R.fail("非法请求");
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(id,currentUserId))
            return R.fail("非法请求");
        return R.ok(projectTaskService.getProjectList(id));
    }

    /**
     * 添加项目任务
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> addProjectTask(@RequestBody ProjectTaskCreateDTO dto){
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
            return R.fail("权限不足，只有项目负责人或管理员可以添加任务");
        projectTaskService.addProjectTask(dto);
        return R.ok();
    }

    /**
     * 修改项目任务
     */
    @PutMapping("/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> updateProjectTask(@PathVariable("projectId") Long projectId,
            @RequestBody ProjectTaskUpdateDTO dto){
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
            return R.fail("权限不足，只有项目负责人或管理员可以修改任务");

        projectTaskService.updateProjectTask(dto);
        return R.ok();
    }
}