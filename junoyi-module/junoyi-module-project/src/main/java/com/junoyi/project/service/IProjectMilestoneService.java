package com.junoyi.project.service;

import com.junoyi.project.domain.vo.ProjectMilestoneVO;

import java.util.List;

/**
 * 项目里程碑业务接口
 *
 * @author Fan
 */
public interface IProjectMilestoneService {

    List<ProjectMilestoneVO> getProjectMilestoneList(Long projectId);
}
