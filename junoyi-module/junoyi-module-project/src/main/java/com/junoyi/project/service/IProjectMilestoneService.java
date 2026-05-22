package com.junoyi.project.service;

import com.junoyi.project.domain.dto.ProjectMilestoneDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.vo.ProjectMilestoneVO;

import java.util.List;

/**
 * 项目里程碑业务接口
 *
 * @author Fan
 */
public interface IProjectMilestoneService {

    /**
     * 获取项目里程碑列表
     * @param projectId 项目ID
     * @return 项目里程碑列表
     */
    List<ProjectMilestoneVO> getProjectMilestoneList(Long projectId);

    /**
     * 添加项目里程碑
     * @param dto 传输数据
     */
    void addProjectMilestone(ProjectMilestoneDTO dto);

    /**
     * 更新项目里程碑
     * @param dto 传输数据
     */
    void updateProjectMilestone(ProjectMilestoneDTO dto);
}
