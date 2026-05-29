package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.task.domain.dto.TaskListQueryDTO;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.po.TaskUser;
import com.junoyi.task.domain.vo.TaskListVO;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.mapper.TaskUserMapper;
import com.junoyi.task.service.ITaskApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务审核业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskApprovalServiceImpl implements ITaskApprovalService {

    private final TaskMapper taskMapper;
    private final TaskUserMapper taskUserMapper;
    private final SysDictApi sysDictApi;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取任务审核列表
     * @param queryDTO 查询参数
     * @param page 分页
     * @return 任务审核列表
     */
    @Override
    public PageResult<TaskListVO> getApprovalList(TaskListQueryDTO queryDTO, Page<Task> page) {
        // 强制设置状态为待验收（status = 2）
        if (queryDTO == null) {
            queryDTO = new TaskListQueryDTO();
        }
        queryDTO.setStatus(2);

        // 使用 XML 中定义的 SQL 查询
        IPage<TaskListVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<TaskListVO> resultPage = taskMapper.selectTaskListPage(voPage, queryDTO);

        List<TaskListVO> records = resultPage.getRecords();

        if (records.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, (int) page.getCurrent(), (int) page.getSize());
        }

        // 批量查询协作人列表
        List<Long> taskIds = records.stream().map(TaskListVO::getId).collect(Collectors.toList());
        Map<Long, List<TaskListVO.TaskUser>> taskUsersMap = batchQueryTaskUsers(taskIds);

        // 批量获取字典数据
        Map<String, SysDictDataVO> statusMap = buildDictMap("task_status");
        Map<String, SysDictDataVO> priorityMap = buildDictMap("task_priority");

        // 填充字典标签和协作人列表
        for (TaskListVO vo : records) {
            // 填充协作人列表
            vo.setTaskUserList(taskUsersMap.getOrDefault(vo.getId(), new ArrayList<>()));

            // 填充状态标签
            if (vo.getStatus() != null) {
                SysDictDataVO statusDict = statusMap.get(String.valueOf(vo.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            // 填充优先级标签
            if (vo.getPriority() != null) {
                SysDictDataVO priorityDict = priorityMap.get(String.valueOf(vo.getPriority()));
                if (priorityDict != null) {
                    vo.setPriorityLabel(priorityDict.getDictLabel());
                    vo.setPriorityType(priorityDict.getListClass());
                }
            }
        }

        return PageResult.of(records, resultPage.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 批量查询任务协作人
     *
     * @param taskIds 任务ID列表
     * @return 任务ID为key，协作人列表为value的Map
     */
    private Map<Long, List<TaskListVO.TaskUser>> batchQueryTaskUsers(List<Long> taskIds) {
        if (taskIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        // 查询协作人关联关系（taskRole = 2 表示协作人）
        LambdaQueryWrapper<TaskUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TaskUser::getTaskId, taskIds)
                .eq(TaskUser::getTaskRole, 2);
        List<TaskUser> taskUsers = taskUserMapper.selectList(wrapper);

        if (taskUsers.isEmpty()) {
            return new java.util.HashMap<>();
        }

        // 批量查询用户信息
        List<Long> userIds = taskUsers.stream()
                .map(TaskUser::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getUserId, u -> u));

        // 按任务ID分组
        return taskUsers.stream()
                .collect(Collectors.groupingBy(
                        TaskUser::getTaskId,
                        Collectors.mapping(
                                tu -> {
                                    TaskListVO.TaskUser taskUser = new TaskListVO.TaskUser();
                                    SysUser user = userMap.get(tu.getUserId());
                                    if (user != null) {
                                        taskUser.setUserId(user.getUserId());
                                        taskUser.setAvatar(user.getAvatar());
                                        taskUser.setNickName(user.getNickName());
                                    }
                                    return taskUser;
                                },
                                Collectors.toList()
                        )
                ));
    }

    /**
     * 构建字典映射表
     *
     * @param dictType 字典类型
     * @return 字典值为key，字典数据为value的Map
     */
    private Map<String, SysDictDataVO> buildDictMap(String dictType) {
        List<SysDictDataVO> dictList = sysDictApi.getDictDataByType(dictType);
        return dictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict, (v1, v2) -> v1));
    }
}