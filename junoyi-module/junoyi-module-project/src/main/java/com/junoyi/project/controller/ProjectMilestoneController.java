package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;
import com.junoyi.project.service.IProjectMilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 获取项目里程碑列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectMilestoneVO>> getProjectMilestone(@PathVariable("projectId") Long projectId){


        return R.ok(projectMilestoneService.getProjectMilestoneList(projectId));
    }
}