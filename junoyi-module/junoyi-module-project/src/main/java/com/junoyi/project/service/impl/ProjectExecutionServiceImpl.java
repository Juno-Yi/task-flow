package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.dto.TaskStatistics;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import com.junoyi.project.event.ProjectRecordEvent;
import com.junoyi.project.exception.ProjectException;
import com.junoyi.project.exception.ProjectNotFoundException;
import com.junoyi.project.mapper.ProjectMapper;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectExecutionService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.task.api.TaskServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活跃项目业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectExecutionServiceImpl implements IProjectExecutionService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;
    private final SysDictApi sysDictApi;
    private final TaskServiceApi taskServiceApi;

    /**
     * 获取项目执行中列表
     * @param queryDTO 查询数据
     * @return 活跃项目列表
     */
    @Override
    public PageResult<ProjectListVO> getExecutionList(ProjectListQueryDTO queryDTO, Page<Project> page) {
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();

        // 判断用户是否拥有查看所有项目数据权限
        boolean hasAllDataPermission = PermissionHelper.hasPermission("project.data.list.all");
        // 如果没有查看所有项目的权限，需要筛选出用户参与的项目
        List<Long> accessibleProjectIds = null;
        if (!hasAllDataPermission) {
            // 查询用户作为成员的项目ID列表
            LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(ProjectMember::getUserId, currentUserId)
                    .eq(ProjectMember::getStatus, 1); // 只查询在职成员
            List<ProjectMember> userProjects = projectMemberMapper.selectList(memberWrapper);

            if (userProjects.isEmpty()) {
                // 用户不是任何项目的成员，返回空结果
                return PageResult.of(
                        new ArrayList<>(),
                        0L,
                        (int) page.getCurrent(),
                        (int) page.getSize()
                );
            }

            accessibleProjectIds = userProjects.stream()
                    .map(ProjectMember::getProjectId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 构建查询条件
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getNo() != null, Project::getNo, queryDTO.getNo())
                .like(StringUtils.isNotBlank(queryDTO.getName()), Project::getName, queryDTO.getName())
                .eq(queryDTO.getType() != null, Project::getType, queryDTO.getType())
                .eq(Project::isDelFlag, false)
                // 核心：只查询活跃状态（1-进行中、2-已暂停、、6-长期维护）
                .in(Project::getStatus, 1, 2, 6);

        // 如果没有查看所有项目的权限，添加项目ID过滤条件
        if (!hasAllDataPermission && accessibleProjectIds != null) {
            wrapper.in(Project::getId, accessibleProjectIds);
        }

        wrapper.orderByDesc(Project::getCreateTime);

        // 分页查询项目列表
        Page<Project> resultPage = projectMapper.selectPage(page, wrapper);
        List<Project> projects = resultPage.getRecords();

        // 如果没有数据，直接返回空结果
        if (projects.isEmpty()) {
            return PageResult.of(
                    new ArrayList<>(),
                    resultPage.getTotal(),
                    (int) resultPage.getCurrent(),
                    (int) resultPage.getSize()
            );
        }

        // 收集项目ID
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());

        // 批量查询项目成员数量
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.in(ProjectMember::getProjectId, projectIds)
                .eq(ProjectMember::getStatus, 1); // 只统计在职成员
        List<ProjectMember> members = projectMemberMapper.selectList(memberWrapper);


        // 按项目ID分组统计成员数量
        Map<Long, Long> memberCountMap = members.stream()
                .collect(Collectors.groupingBy(ProjectMember::getProjectId, Collectors.counting()));

        // 收集负责人ID
        List<Long> leaderIds = projects.stream()
                .map(Project::getLeader)
                .filter(leaderId -> leaderId != null)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询负责人信息
        Map<Long, String> leaderNameMap = new HashMap<>();
        if (!leaderIds.isEmpty()) {
            leaderNameMap = sysUserMapper.selectBatchIds(leaderIds).stream()
                    .collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName));
        }

        // 批量查询项目任务统计
        Map<Long, TaskStatistics> taskStatisticsMap = calculateTaskStatistics(projectIds);

        // 批量查询字典数据（避免 N+1 查询）
        List<SysDictDataVO> projectTypeDictList = sysDictApi.getDictDataByType("project_type");
        List<SysDictDataVO> projectStatusDictList = sysDictApi.getDictDataByType("project_status");
        List<SysDictDataVO> projectPriorityDictList = sysDictApi.getDictDataByType("project_priority");

        // 转换为 Map 便于查找
        Map<String, SysDictDataVO> projectTypeDictMap = projectTypeDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));
        Map<String, SysDictDataVO> projectStatusDictMap = projectStatusDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));
        Map<String, SysDictDataVO> projectPriorityDictMap = projectPriorityDictList.stream()
                .collect(Collectors.toMap(SysDictDataVO::getDictValue, dict -> dict));

        // 转换为VO并填充数据
        List<ProjectListVO> voList = new ArrayList<>();
        for (Project project : projects) {
            ProjectListVO vo = new ProjectListVO();
            BeanUtils.copyProperties(project, vo);

            // 设置负责人名称
            vo.setLeaderName(leaderNameMap.getOrDefault(project.getLeader(), "未知"));

            // 设置成员数量
            vo.setMemberCount(memberCountMap.getOrDefault(project.getId(), 0L).intValue());

            // 设置任务统计和进度
            TaskStatistics taskStats = taskStatisticsMap.get(project.getId());
            if (taskStats != null) {
                vo.setTotalTasks(taskStats.getTotalTasks());
                vo.setCompletedTasks(taskStats.getCompletedTasks());
                vo.setProgress(taskStats.getProgress());
            } else {
                vo.setTotalTasks(0);
                vo.setCompletedTasks(0);
                vo.setProgress(0);
            }

            // 字典翻译 - 项目类型（使用预加载的字典数据）
            if (project.getType() != null) {
                SysDictDataVO typeDict = projectTypeDictMap.get(String.valueOf(project.getType()));
                if (typeDict != null) {
                    vo.setTypeLabel(typeDict.getDictLabel());
                    vo.setTypeLabelType(typeDict.getListClass());
                }
            }

            // 字典翻译 - 项目状态（使用预加载的字典数据）
            if (project.getStatus() != null) {
                SysDictDataVO statusDict = projectStatusDictMap.get(String.valueOf(project.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            // 字典翻译 - 项目优先级（使用预加载的字典数据）
            if (project.getPriority() != null) {
                SysDictDataVO priorityDict = projectPriorityDictMap.get(String.valueOf(project.getPriority()));
                if (priorityDict != null) {
                    vo.setPriorityLabel(priorityDict.getDictLabel());
                    vo.setPriorityType(priorityDict.getListClass());
                }
            }

            voList.add(vo);
        }

        // 返回分页结果
        return PageResult.of(
                voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }


    /**
     * 批量计算项目任务统计信息
     * @param projectIds 项目ID列表
     * @return 项目任务统计信息Map，key为项目ID，value为任务统计信息
     */
    private Map<Long, TaskStatistics> calculateTaskStatistics(List<Long> projectIds) {
        Map<Long, TaskStatistics> statisticsMap = new HashMap<>();

        for (Long projectId : projectIds) {
            // 获取项目任务列表
            List<com.junoyi.task.domain.vo.ProjectTaskItemVO> taskList = taskServiceApi.getProjectTaskList(projectId);

            // 统计任务数量
            int totalTasks = taskList.size();
            int completedTasks = 0;

            // 统计已完成任务数量（状态为4表示已完成）
            for (com.junoyi.task.domain.vo.ProjectTaskItemVO task : taskList) {
                if (task.getStatus() != null && task.getStatus() == 4) {
                    completedTasks++;
                }
            }

            // 计算进度百分比
            int progress = 0;
            if (totalTasks > 0) {
                progress = (int) Math.round((double) completedTasks / totalTasks * 100);
            }

            TaskStatistics statistics = new TaskStatistics();
            statistics.setTotalTasks(totalTasks);
            statistics.setCompletedTasks(completedTasks);
            statistics.setProgress(progress);

            statisticsMap.put(projectId, statistics);
        }

        return statisticsMap;
    }

    /**
     * 发起项目验收
     * @param projectId 项目ID
     */
    @Override
    public void initiateAcceptance(Long projectId) {
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null || project.isDelFlag()){
            throw new ProjectNotFoundException("不存在的项目");
        }

        // 项目状态是否进行中（状态1表示进行中）
        if (project.getStatus() != 1){
            throw new ProjectException("项目状态不是进行中，无法提交验收");
        }

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        boolean hasPermission = PermissionHelper.isSuperAdmin()
                || PermissionHelper.hasPermission("project.ui.execution.initiate.acceptance.button")
                || project.getLeader().equals(currentUserId);
        if (!hasPermission) {
            throw new ProjectException("无权限启动该项目，只有项目负责人或管理员可以发起项目验收");
        }

        // 更新项目状态为待验收（状态3）
        project.setStatus(3);
        project.setUpdateBy(SecurityUtils.getUserName());
        project.setUpdateTime(DateUtils.getNowDate());
        projectMapper.updateById(project);

        // TODO 发布项目动态

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("initiate-acceptance", "project",
                "项目「" + project.getName() + "」（编号：" + project.getNo() + "）发起验收，等待验收",
                String.valueOf(project.getId()), project.getName()));
    }

    /**
     * 暂停项目
     * @param projectId 项目ID
     */
    @Override
    public void pauseProject(Long projectId) {
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null || project.isDelFlag()){
            throw new ProjectNotFoundException("不存在的项目");
        }

        // 项目状态是否进行中（状态1表示进行中）
        if (project.getStatus() != 1){
            throw new ProjectException("项目状态不是进行中，无法暂停");
        }

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        boolean hasPermission = PermissionHelper.isSuperAdmin()
                || PermissionHelper.hasPermission("project.ui.execution.pause.button")
                || project.getLeader().equals(currentUserId);
        if (!hasPermission) {
            throw new ProjectException("无权限启动该项目，只有项目负责人或管理员可以暂停项目");
        }

        // 更新项目状态为暂停（状态2）
        project.setStatus(2);
        project.setUpdateBy(SecurityUtils.getUserName());
        project.setUpdateTime(DateUtils.getNowDate());
        projectMapper.updateById(project);

        // 发布项目日志
        EventBus.get().callEvent(new ProjectRecordEvent(
                projectId,
                SecurityUtils.getUserId(),
                ProjectRecordType.PAUSE_PROJECT,
                ProjectRecordTargetType.PROJECT,
                "暂停了项目「" + project.getName() + "」(编号：" + project.getNo() + "）"
        ));

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("pause", "project",
                "暂停了项目「" + project.getName() + "」（编号：" + project.getNo() + "）",
                String.valueOf(project.getId()), project.getName()));
    }

    /**
     * 取消暂停项目
     * @param projectId 项目ID
     */
    @Override
    public void cancelPauseProject(Long projectId) {
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null || project.isDelFlag()){
            throw new ProjectNotFoundException("不存在的项目");
        }

        // 项目状态是否暂停（状态2表示暂停）
        if (project.getStatus() != 2){
            throw new ProjectException("项目状态不是暂停，无法暂停");
        }

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();
        boolean hasPermission = PermissionHelper.isSuperAdmin()
                || PermissionHelper.hasPermission("project.ui.execution.pause.cancel.button")
                || project.getLeader().equals(currentUserId);
        if (!hasPermission) {
            throw new ProjectException("无权限启动该项目，只有项目负责人或管理员可以取消暂停");
        }

        // 更新项目状态为进行中（状态1）
        project.setStatus(1);
        project.setUpdateBy(SecurityUtils.getUserName());
        project.setUpdateTime(DateUtils.getNowDate());
        projectMapper.updateById(project);

        // 发布项目日志
        EventBus.get().callEvent(new ProjectRecordEvent(
                projectId,
                SecurityUtils.getUserId(),
                ProjectRecordType.PAUSE_PROJECT,
                ProjectRecordTargetType.PROJECT,
                "取消暂停项目「" + project.getName() + "」(编号：" + project.getNo() + "）"
        ));

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("cancel-pause", "project",
                "取消暂停了项目「" + project.getName() + "」（编号：" + project.getNo() + "）",
                String.valueOf(project.getId()), project.getName()));
    }
}