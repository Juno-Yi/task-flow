package com.junoyi.task.api;

import com.junoyi.task.domain.dto.ProjectTaskCreateDTO;
import com.junoyi.task.domain.dto.ProjectTaskUpdateDTO;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;

import java.util.List;

/**
 * 任务业务API接口
 * 供其他模块调用，例如创建任务，修改任务等
 *
 * @author Fan
 */
public interface ITaskServiceApi {

    /**
     * 获取项目任务列表
     * @param projectId 项目ID
     * @return 项目任务列表
     */
    List<ProjectTaskItemVO> getProjectTaskList(Long projectId);

    /**
     * 创建项目任务
     * @param projectTaskCreateDTO 项目任务创建数据传递对象
     */
    void createProjectTask(ProjectTaskCreateDTO projectTaskCreateDTO);

    /**
     * 更新项目任务
     * @param projectTaskUpdateDTO 项目任务更新数据传递对象
     */
    void updateProjectTask(ProjectTaskUpdateDTO projectTaskUpdateDTO);

    /**
     * 根据任务ID获取项目ID
     * @param taskId 任务ID
     * @return 项目ID，如果任务不存在或不是项目任务则返回null
     */
    Long getProjectIdByTaskId(Long taskId);

    /**
     * 获取项目任务总数量
     * @param projectId 项目ID
     * @return 任务总数
     */
    Long getProjectTaskCount(Long projectId);

    /**
     * 获取项目未完成的任务总数量
     * @param projectId 项目ID
     * @return 未完成任务总数
     */
    Long getProjectUnfinishedTaskCount(Long projectId);
}
