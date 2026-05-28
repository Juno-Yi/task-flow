package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 *  项目结后业务接口
 *
 * @author Fan
 */
public interface IProjectEndService {

    /**
     * 获取项目结后列表
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 结后列表
     */
    PageResult<ProjectListVO> getEndList(ProjectListQueryDTO queryDTO, Page<Project> page);
}
