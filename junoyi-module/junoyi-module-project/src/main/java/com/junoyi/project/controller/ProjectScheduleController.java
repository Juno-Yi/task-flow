package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectGanttQueryDTO;
import com.junoyi.project.domain.vo.ProjectGanttVO;
import com.junoyi.project.service.IProjectScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目日程控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/schedule")
@RequiredArgsConstructor
public class ProjectScheduleController extends BaseController {

    private final IProjectScheduleService projectScheduleService;

    /**
     * 获取活跃中的项目日程甘特图列表
     */
    @GetMapping("/gantt/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectGanttVO>> getActiveProjectGanttList(ProjectGanttQueryDTO queryDTO){
        return R.ok(projectScheduleService.getActiveProjectGantList(queryDTO));
    }
}