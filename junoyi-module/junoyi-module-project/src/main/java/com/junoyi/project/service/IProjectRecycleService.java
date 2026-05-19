package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 * 项目回收站业务接口
 *
 * @author Fan
 */
public interface IProjectRecycleService {

    /**
     * 获取项目回收站列表
     * @param queryDTO 查询参数
     * @return 项目回收站列表
     */
    PageResult<ProjectListVO> getRecycleList(ProjectListQueryDTO queryDTO, Page<Project> page);

    /**
     * 恢复已删除项目
     * @param projectId 项目ID
     */
    void restore(Long projectId);
}
