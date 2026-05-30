package com.junoyi.project.service;

import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;

import java.util.List;

/**
 * 项目任务业务接口
 *
 * @author Fan
 */
public interface IProjectTaskService {

    /**
     * 获取项目的任务列表
     * @param projectId 项目Id
     * @return 项目任务列表
     */
    List<ProjectTaskItemVO> getProjectList(Long projectId);

    /**
     * 创建项目任务
     * @param dto 创建任务传输数据
     */
    void addProjectTask(ProjectTaskCreateDTO dto);

    /**
     * 更新项目任务
     * @param dto 更新任务传输数据
     */
    void updateProjectTask(ProjectTaskUpdateDTO dto);
}
