package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectRequirementQueryDTO;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import com.junoyi.project.service.IProjectPermissionService;
import com.junoyi.project.service.IProjectRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}