package com.junoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import com.junoyi.task.domain.vo.TaskItemVO;
import com.junoyi.task.domain.vo.TaskListDetailVO;
import com.junoyi.task.domain.vo.TaskListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表 Mapper
 *
 * @author Fan
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 分页查询任务列表
     *
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @return 任务列表
     */
    IPage<TaskListVO> selectTaskListPage(IPage<TaskListVO> page, @Param("query") TaskListQueryDTO queryDTO);

    /**
     * 根据任务ID查询任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskListDetailVO selectTaskDetailById(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询负责人
     *
     * @param taskId 任务ID
     * @return 负责人
     */
    TaskListVO.OwnerUser selectOwnerUserByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询任务执行人列表
     *
     * @param taskId 任务ID
     * @return 执行人列表
     */
    List<TaskListVO.TaskUser> selectTaskUsersByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询详情负责人
     *
     * @param taskId 任务ID
     * @return 负责人
     */
    TaskListDetailVO.OwnerUser selectDetailOwnerUserByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询详情执行人列表
     *
     * @param taskId 任务ID
     * @return 执行人列表
     */
    List<TaskListDetailVO.TaskUser> selectDetailTaskUsersByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询任务记录列表
     *
     * @param taskId 任务ID
     * @return 任务记录列表
     */
    List<TaskListDetailVO.RecordItem> selectTaskRecordListByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据记录ID查询附件列表
     *
     * @param recordId 记录ID
     * @return 附件列表
     */
    List<TaskListDetailVO.AttachmentItem> selectTaskAttachmentsByRecordId(@Param("recordId") Long recordId);

    /**
     * 查询当前月份与任务时间区间有交集的任务列表
     *
     * @param userId 用户ID
     * @param monthStart 当前月开始时间
     * @param monthEnd 当前月结束时间
     * @return 任务列表
     */
    List<TaskItemVO> selectCurrentMonthTaskList(@Param("userId") Long userId,
                                                @Param("monthStart") java.util.Date monthStart,
                                                @Param("monthEnd") java.util.Date monthEnd);

    Long countTaskUserRelation(@Param("taskId") Long taskId, @Param("userId") Long userId);

    TaskItemVO.OwnerUser selectItemOwnerUserByTaskId(@Param("taskId") Long taskId);

    List<TaskItemVO.TaskUser> selectItemTaskUsersByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据项目ID查询项目任务列表
     *
     * @param projectId 项目ID
     * @return 项目任务列表
     */
    List<ProjectTaskItemVO> selectProjectTaskList(@Param("projectId") Long projectId);

    /**
     * 根据任务ID查询项目任务负责人
     *
     * @param taskId 任务ID
     * @return 负责人
     */
    ProjectTaskItemVO.OwnerUser selectProjectTaskOwnerUserByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询项目任务执行人列表
     *
     * @param taskId 任务ID
     * @return 执行人列表
     */
    List<ProjectTaskItemVO.TaskUser> selectProjectTaskUsersByTaskId(@Param("taskId") Long taskId);
}
