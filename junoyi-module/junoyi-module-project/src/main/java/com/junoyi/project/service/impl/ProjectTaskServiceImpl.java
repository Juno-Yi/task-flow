package com.junoyi.project.service.impl;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import com.junoyi.project.event.ProjectRecordEvent;
import com.junoyi.project.exception.ProjectException;
import com.junoyi.project.service.IProjectTaskService;
import com.junoyi.task.api.TaskServiceApi;
import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目任务业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectTaskServiceImpl implements IProjectTaskService {

    private final TaskServiceApi taskServiceApi;

    /**
     * 获取项目的任务列表
     * @param projectId 项目Id
     * @return 项目任务列表
     */
    @Override
    public List<ProjectTaskItemVO> getProjectList(Long projectId) {
        if (projectId == null || projectId == 0L)
            throw new ProjectException("项目ID不能为空");

        return taskServiceApi.getProjectTaskList(projectId);
    }

    /**
     * 创建项目任务
     * @param dto 创建任务传输数据
     */
    @Override
    public void addProjectTask(ProjectTaskCreateDTO dto) {
        if (dto.getProjectId() == null || dto.getProjectId() == 0L) {
            throw new ProjectException("项目ID不能为空");
        }
        taskServiceApi.createProjectTask(dto);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                dto.getProjectId(),
                SecurityUtils.getUserId(),
                ProjectRecordType.CREATE_TASK,
                ProjectRecordTargetType.TASK,
                "创建了任务「" + dto.getTitle() + "」"
        ));
    }

    /**
     * 更新项目任务
     * @param projectId 项目ID
     * @param dto 更新任务传输数据
     */
    @Override
    public void updateProjectTask(Long projectId, ProjectTaskUpdateDTO dto) {
        if (dto.getId() == null) {
            throw new ProjectException("任务ID不能为空");
        }
        taskServiceApi.updateProjectTask(dto);

        // 发布项目动态
        EventBus.get().callEvent(new ProjectRecordEvent(
                projectId,
                SecurityUtils.getUserId(),
                ProjectRecordType.UPDATE_TASK,
                ProjectRecordTargetType.TASK,
                "更新了任务「" + dto.getTitle() + "」"
        ));
    }
}