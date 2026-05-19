package com.junoyi.project.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.project.domain.dto.ProjectDeleteDTO;
import com.junoyi.project.domain.dto.ProjectListDTO;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.dto.ProjectOptionQueryDTO;
import com.junoyi.project.domain.vo.ProjectInfoVO;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.domain.vo.ProjectOptionVO;
import com.junoyi.project.service.IProjectListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目列表控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/project/list")
@RequiredArgsConstructor
public class ProjectListController extends BaseController {

    private final IProjectListService projectListService;

    /**
     * 获取项目列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<ProjectListVO>> getList(ProjectListQueryDTO queryDTO){
        return R.ok(projectListService.getProjectList(queryDTO,buildPage()));
    }

    /**
     * 获取项目下拉框选项列表
     */
    @GetMapping("/options")
    public R<List<ProjectOptionVO>> getOptions(ProjectOptionQueryDTO queryDTO){
        return R.ok();
    }

    /**
     * 添加项目
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.list.add.button"
    )
    public R<Void> addProject(@RequestBody ProjectListDTO dto){
        projectListService.addProject(dto);
        return R.ok();
    }

    /**
     * 修改项目
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = "project.ui.list.edit.button"
    )
    public R<Void> updateProject(@RequestBody ProjectListDTO dto){
        projectListService.updateProject(dto);
        return R.ok();
    }

    /**
     * 删除项目（软删除，需要密码验证）
     */
    @PostMapping("/delete/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"project.ui.list.delete.button"}
    )
    public R<Void> deleteProjectRepo(@PathVariable("id") Long id, @RequestBody ProjectDeleteDTO deleteDTO){
        projectListService.deleteProjectRepo(id, deleteDTO.getPassword());
        return R.ok();
    }

    /**
     * 批量删除项目（软删除，需要密码验证）
     */
    @PostMapping("/delete/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"project.ui.list.delete.button"}
    )
    public R<Void> deleteProjectRepoBatch(@RequestBody ProjectDeleteDTO deleteDTO){
        projectListService.deleteProjectRepoBatch(deleteDTO.getIds(), deleteDTO.getPassword());
        return R.ok();
    }

    /**
     * 获取项目信息
     */
    @GetMapping("/info/{projectId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<ProjectInfoVO> getProjectInfo(@PathVariable("projectId") Long projectId){

        return R.ok();
    }
}