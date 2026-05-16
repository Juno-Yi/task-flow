package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 * 已归档项目业务接口
 *
 * @author Fan
 */
public interface IProjectArchivedService {

    /**
     * 获取已归档项目列表
     * @param queryDTO 查询数据
     * @return 已归档项目列表
     */
    PageResult<ProjectListVO> getArchivedList(ProjectListQueryDTO queryDTO, Page<Project> page);
}