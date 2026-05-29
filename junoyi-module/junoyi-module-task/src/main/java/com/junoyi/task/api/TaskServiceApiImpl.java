package com.junoyi.task.api;

import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务业务API接口实现
 * 任务模块中实现业务接口
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskServiceApiImpl implements ITaskServiceApi {

    private final TaskMapper taskMapper;
    private final TaskUserMapper taskUserMapper;

    /**
     * 获取项目任务列表
     * @param projectId 项目ID
     * @return 项目任务列表
     */
    @Override
    public List<ProjectTaskItemVO> getProjectTaskList(Long projectId) {
        return List.of();
    }

    /**
     * 创建项目任务
     * @param projectTaskCreateDTO 项目任务创建数据传递对象
     */
    @Override
    public void createProjectTask(ProjectTaskCreateDTO projectTaskCreateDTO) {

    }

    /**
     * 更新项目任务
     * @param projectTaskUpdateDTO 项目任务更新数据传递对象
     */
    @Override
    public void updateProjectTask(ProjectTaskUpdateDTO projectTaskUpdateDTO) {

    }

    /**
     * 根据任务ID获取项目ID
     * @param taskId 任务ID
     * @return 项目ID，如果任务不存在或不是项目任务则返回null
     */
    @Override
    public Long getProjectIdByTaskId(Long taskId) {
        return 0L;
    }
}