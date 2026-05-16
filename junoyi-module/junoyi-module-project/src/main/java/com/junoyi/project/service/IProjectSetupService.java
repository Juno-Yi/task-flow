package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 * 项目立项业务接口
 *
 * @author Fan
 */
public interface IProjectSetupService {

    /**
     * 获取立项项目列表
     * @param queryDTO 查询参数
     * @return 立项项目列表
     */
    PageResult<ProjectListVO> getApprovalList(ProjectListQueryDTO queryDTO, Page<Project> page);

    /**
     * 启动项目
     * @param projectId 项目ID
     */
    void startProject(Long projectId);
}