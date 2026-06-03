package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectAcceptanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目结项控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/acceptance")
@RequiredArgsConstructor
public class ProjectAcceptanceController extends BaseController {

    private final IProjectAcceptanceService projectAcceptanceService;

    /**
     * 获取项目结项列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getProjectAcceptanceList(ProjectListQueryDTO queryDTO){
        return R.ok(projectAcceptanceService.getAcceptanceList(queryDTO,buildPage()));
    }


}