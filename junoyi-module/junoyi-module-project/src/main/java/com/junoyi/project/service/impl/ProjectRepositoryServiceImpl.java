package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.project.convert.ProjectRepositoryConverter;
import com.junoyi.project.domain.po.ProjectRepository;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;
import com.junoyi.project.mapper.ProjectRepositoryMapper;
import com.junoyi.project.service.IProjectRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目仓库业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectRepositoryServiceImpl implements IProjectRepositoryService {

    private final ProjectRepositoryMapper projectRepositoryMapper;

    /**
     * 获取项目仓库列表
     * @param projectId 项目ID
     * @return 项目仓库列表
     */
    @Override
    public List<ProjectRepositoryVO> getRepositoryList(Long projectId) {
        // 查询仓库列表
        LambdaQueryWrapper<ProjectRepository> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectRepository::getProjectId, projectId)
                .orderByDesc(ProjectRepository::getIsMain)
                .orderByAsc(ProjectRepository::getId);

        List<ProjectRepository> repositories = projectRepositoryMapper.selectList(wrapper);

        // 转换为 VO
        List<ProjectRepositoryVO> voList = repositories.stream()
                .map(ProjectRepositoryConverter::toVO)
                .collect(Collectors.toList());

        return voList;
    }
}