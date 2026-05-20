package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectDetailVO;
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

    /**
     * 获取项目详情信息（通过项目编号no)
     */
    @GetMapping("/{projectNo}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<ProjectDetailVO> getProjectDetailByNo(@PathVariable("projectNo") Long projectNo){
        return R.ok();
    }
}