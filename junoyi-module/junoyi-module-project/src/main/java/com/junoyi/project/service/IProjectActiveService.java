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
public interface IProjectActiveService {

    /**
     * 获取活跃项目列表
     * @param queryDTO 查询数据
     * @return 活跃项目列表
     */
    PageResult<ProjectListVO> getActiveList(ProjectListQueryDTO queryDTO, Page<Project> page);
}
