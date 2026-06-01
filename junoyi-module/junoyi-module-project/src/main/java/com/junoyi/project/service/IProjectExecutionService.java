package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 * 活跃项目业务接口
 *
 * @author Fan
 */
public interface IProjectExecutionService {

    /**
     * 获取项目执行中列表
     * @param queryDTO 查询数据
     * @return 活跃项目列表
     */
    PageResult<ProjectListVO> getExecutionList(ProjectListQueryDTO queryDTO, Page<Project> page);

    /**
     * 发起项目验收
     * @param projectId 项目ID
     */
    void initiateAcceptance(Long projectId);

    /**
     * 暂停项目
     * @param projectId 项目ID
     */
    void pauseProject(Long projectId);

}
