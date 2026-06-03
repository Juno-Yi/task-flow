package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectEndService;
import lombok.RequiredArgsConstructor;
import org.simpleframework.xml.Path;
import org.springframework.web.bind.annotation.*;

/**
 * 项目结后控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/end")
@RequiredArgsConstructor
public class ProjectEndController extends BaseController {

    private final IProjectEndService projectEndService;

    /**
     * 获取项目节后列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getEndList(ProjectListQueryDTO queryDTO){
        return R.ok(projectEndService.getEndList(queryDTO,buildPage()));
    }

    /**
     * 项目归档
     */
    @PostMapping("/{projectId}/archive")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.end.archive.button"
    )
    public R<Void> archiveProject(@PathVariable("projectId") Long projectId){
        projectEndService.archive(projectId);
        return R.ok();
    }
}