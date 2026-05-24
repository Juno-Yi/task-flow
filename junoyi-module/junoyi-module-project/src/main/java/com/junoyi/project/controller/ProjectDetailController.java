package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectDetailVO;
import com.junoyi.project.domain.vo.ProjectOverviewVO;
import com.junoyi.project.service.IProjectDetailService;
import com.junoyi.project.service.IProjectPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目详情控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/detail")
@RequiredArgsConstructor
public class ProjectDetailController extends BaseController {

    private final IProjectDetailService projectDetailService;
    private final IProjectPermissionService projectPermissionService;

    /**
     * 获取项目详情信息（通过项目编号no)
     */
    @GetMapping("/{projectNo}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<ProjectDetailVO> getProjectDetailByNo(@PathVariable("projectNo") String projectNo){
        if (StringUtils.isEmpty(projectNo))
            return R.fail("非法请求");

        // 在进入业务层之前，先去判断一下用户是否有该项目的查看权限，防止水平越权
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectNo,currentUserId))
            return R.fail("非法请求");

        return R.ok(projectDetailService.getProjectDetailByNo(projectNo));
    }

    /**
     * 获取项目详情概览数据
     */
    @GetMapping("/{projectNo}/overview")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<ProjectOverviewVO> getOverviewData(@PathVariable("projectNo") String projectNo){
        if (StringUtils.isEmpty(projectNo))
            return R.fail("非法请求");

        // 在进入业务层之前，先去判断一下用户是否有该项目的查看权限，防止水平越权
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || currentUserId == 0L)
            return R.fail("非法请求");
        if (!projectPermissionService.hasProjectViewPermission(projectNo,currentUserId))
            return R.fail("非法请求");


        return R.ok(projectDetailService.getProjectOverview(projectNo));
    }
}