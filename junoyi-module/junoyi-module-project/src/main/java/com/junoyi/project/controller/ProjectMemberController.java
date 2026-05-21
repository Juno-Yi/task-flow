package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.service.IProjectMemberService;
import com.junoyi.project.service.IProjectPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}