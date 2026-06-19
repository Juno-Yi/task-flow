package com.junoyi.task.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.task.domain.vo.TaskAnalysisVO;
import com.junoyi.task.domain.vo.TaskStatusOverviewVO;
import com.junoyi.task.service.ITaskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 任务分析统计控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/task/analysis")
@RequiredArgsConstructor
public class TaskAnalysisController extends BaseController {

    private final ITaskAnalysisService taskAnalysisService;

    @GetMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.analysis.view"
    )
    public R<TaskAnalysisVO> getTaskAnalysis(){
        return R.ok();
    }

    /**
     * 获取任务状态总览数据
     */
    @GetMapping("/status-overview")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "task.ui.analysis.view"
    )
    public R<TaskStatusOverviewVO> getTaskStatusOverview(){
        return R.ok(taskAnalysisService.getTaskStatusOverview());
    }
}