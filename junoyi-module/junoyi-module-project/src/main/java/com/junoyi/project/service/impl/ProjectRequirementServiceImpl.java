package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectRequirementQueryDTO;
import com.junoyi.project.domain.po.ProjectRequirement;
import com.junoyi.project.domain.vo.ProjectRequirementVO;
import com.junoyi.project.mapper.ProjectRequirementMapper;
import com.junoyi.project.service.IProjectRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目需求业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRequirementServiceImpl implements IProjectRequirementService {

    private final ProjectRequirementMapper projectRequirementMapper;

    /**
     * 获取项目需求列表（分页）
     * @param projectId 项目ID
     * @param queryDTO 查询仓鼠
     * @param page 分页
     * @return 需求列表
     */
    @Override
    public PageResult<ProjectRequirementVO> getRequirementList(Long projectId,
                                                               ProjectRequirementQueryDTO queryDTO,
                                                               Page<ProjectRequirement> page) {

        return null;
    }
}