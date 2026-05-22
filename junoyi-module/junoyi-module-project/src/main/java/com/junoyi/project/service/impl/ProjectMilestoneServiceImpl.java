package com.junoyi.project.service.impl;

import com.junoyi.project.domain.vo.ProjectMilestoneVO;
import com.junoyi.project.service.IProjectMilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目里程碑业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectMilestoneServiceImpl implements IProjectMilestoneService {

    @Override
    public List<ProjectMilestoneVO> getProjectMilestoneList(Long projectId) {
        return List.of();
    }
}