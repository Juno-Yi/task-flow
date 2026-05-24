package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectRequirementQueryDTO;
import com.junoyi.project.domain.po.ProjectRequirement;
import com.junoyi.project.domain.vo.ProjectRequirementVO;

/**
 * 项目需求业务接口
 *
 * @author Fan
 */
public interface IProjectRequirementService {

    /**
     * 获取项目需求列表（分页）
     * @param projectId 项目ID
     * @param queryDTO 查询仓鼠
     * @param page 分页
     * @return 需求列表
     */
    PageResult<ProjectRequirementVO> getRequirementList(Long projectId,ProjectRequirementQueryDTO queryDTO, Page<ProjectRequirement> page);
}
