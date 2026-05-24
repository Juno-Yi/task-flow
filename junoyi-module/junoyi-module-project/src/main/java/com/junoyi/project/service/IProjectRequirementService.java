package com.junoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectRequirementDTO;
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

    /**
     * 添加项目需求
     * @param projectId 项目ID
     * @param dto 传输数据
     */
    void addRequirement(Long projectId, ProjectRequirementDTO dto);

    /**
     * 修改项目需求
     * @param projectId 项目ID
     * @param dto 传输数据
     */
    void updateRequirement(Long projectId, ProjectRequirementDTO dto);

    /**
     * 删除项目需求
     * @param projectId 项目ID
     * @param requirementId 项目需求ID
     */
    void deleteRequirement(Long projectId, Long requirementId);
}
