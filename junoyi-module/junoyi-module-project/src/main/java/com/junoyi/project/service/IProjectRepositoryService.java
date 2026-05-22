package com.junoyi.project.service;

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
}
