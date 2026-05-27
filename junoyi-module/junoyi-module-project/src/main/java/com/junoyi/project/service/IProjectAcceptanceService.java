package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;

/**
 * 项目结项业务接口
 *
 * @author Fan
 */
public interface IProjectAcceptanceService {

    /**
     * 获取结项列表
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 结项列表
     */
    PageResult<ProjectListVO> getAcceptanceList(ProjectListQueryDTO queryDTO, Page<Project> page);
}
