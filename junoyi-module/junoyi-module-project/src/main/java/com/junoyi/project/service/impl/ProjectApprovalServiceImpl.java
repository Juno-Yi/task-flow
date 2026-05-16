package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.mapper.ProjectApprovalMapper;
import com.junoyi.project.service.IProjectApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目立项业务接口
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectApprovalServiceImpl implements IProjectApprovalService {

    private final ProjectApprovalMapper projectApprovalMapper;

    /**
     * 获取立项项目列表
     * @param queryDTO 查询参数
     * @return 立项项目列表
     */
    @Override
    public PageResult<ProjectListVO> getApprovalList(ProjectListQueryDTO queryDTO, Page<Project> page) {
        return null;
    }
}