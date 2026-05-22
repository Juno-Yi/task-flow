package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;
import com.junoyi.project.service.IProjectRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目仓库控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/repository")
@RequiredArgsConstructor
public class ProjectRepositoryController extends BaseController {

    private final IProjectRepositoryService projectRepositoryService;

    /**
     * 根据项目ID获取仓库列表
     */
    @GetMapping("/list/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectRepositoryVO>> getRepositoryList(@PathVariable("projectId") Long projectId) {

        // 权限校验

        return R.ok(projectRepositoryService.getRepositoryList(projectId));
    }
}