package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectRecordQueryDTO;
import com.junoyi.project.domain.vo.ProjectRecordVO;
import com.junoyi.project.service.IProjectRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目动态记录控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/record")
@RequiredArgsConstructor
public class ProjectRecordController extends BaseController {

    private final IProjectRecordService projectRecordService;

    /**
     * 获取项目动态列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectRecordVO>> getProjectRecordList(ProjectRecordQueryDTO queryDTO){
        return R.ok(projectRecordService.getProjectRecordList(
                queryDTO.getProjectNo(),
                buildPage()
        ));
    }
}