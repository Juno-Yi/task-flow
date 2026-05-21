package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.service.IProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目成员控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class ProjectMemberController extends BaseController {

    private final IProjectMemberService projectMemberService;

    /**
     * 获取项目成员列表
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<List<ProjectMemberVO>> getProjectMemberList(){
        return R.ok();
    }
}