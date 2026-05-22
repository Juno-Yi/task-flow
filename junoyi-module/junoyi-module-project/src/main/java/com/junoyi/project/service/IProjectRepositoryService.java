package com.junoyi.project.service;

import com.junoyi.project.domain.dto.ProjectRepositoryDTO;
import com.junoyi.project.domain.vo.ProjectRepositoryVO;

import java.util.List;

/**
 * 项目仓库业务接口
 *
 * @author Fan
 */
public interface IProjectRepositoryService {

    /**
     * 获取项目仓库列表
     * @param projectId 项目ID
     * @return 项目仓库列表
     */
    List<ProjectRepositoryVO> getRepositoryList(Long projectId);

    /**
     * 添加项目仓库
     * @param dto 传输数据
     */
    void addRepository(ProjectRepositoryDTO dto);

    /**
     * 更新项目仓库
     * @param dto 传输数据
     */
    void updateRepository(ProjectRepositoryDTO dto);
}
