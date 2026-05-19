package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.service.IProjectRecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 项目回收站控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/recycle")
@RequiredArgsConstructor
public class ProjectRecycleController extends BaseController {

    private final IProjectRecycleService projectRecycleService;

    /**
     * 获取已软删除的项目列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getProjectRecycleList(ProjectListQueryDTO queryDTO){
        return R.ok(projectRecycleService.getRecycleList(queryDTO,buildPage()));
    }

    /**
     * 恢复已经删除的项目
     */
    @PostMapping("/{projectId}/restore")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<Void> restore(@PathVariable("projectId") Long projectId){
        if (projectId == null || projectId == 0)
            return R.fail("非法参数");
        projectRecycleService.restore(projectId);
        return R.ok();
    }
}