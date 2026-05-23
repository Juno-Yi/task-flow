package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import com.junoyi.project.service.IProjectRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 获取项目需求列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectRequirementVO>> getRequirementList(){
        return R.ok();
    }
}