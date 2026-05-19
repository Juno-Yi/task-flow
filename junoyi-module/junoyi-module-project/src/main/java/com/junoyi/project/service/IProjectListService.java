package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListDTO;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.dto.ProjectOptionQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.domain.vo.ProjectOptionVO;

import java.util.List;

/**
 * 项目列表业务接口
 *
 * @author Fan
 */
public interface IProjectListService {

    /**
     * 查询项目列表（分页）
     * @param queryDTO 查询仓鼠
     * @param page 分页参数
     * @return 项目分页结果
     */
    PageResult<ProjectListVO> getProjectList(ProjectListQueryDTO queryDTO, Page<Project> page);

    /**
     * 获取项目下拉列表
     * @param queryDTO 模糊查询参数
     * @return 项目下拉列表
     */
    List<ProjectOptionVO> getProjectOptionList(ProjectOptionQueryDTO queryDTO);

    /**
     * 添加项目
     * @param dto 项目传输数据
     */
    void addProject(ProjectListDTO dto);

    /**
     * 修改项目
     * @param dto 项目传输数据
     */
    void updateProject(ProjectListDTO dto);

    /**
     * 删除项目（软删除，需要密码验证）
     * @param id 项目ID
     * @param password 密码
     */
    void deleteProjectRepo(Long id, String password);

    /**
     * 批量删除项目（软删除，需要密码验证）
     * @param ids 项目ID列表
     * @param password 密码
     */
    void deleteProjectRepoBatch(List<Long> ids, String password);

}
