package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.domain.dto.ProjectGanttQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectGanttVO;
import com.junoyi.project.mapper.ProjectMapper;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectScheduleService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.task.api.TaskServiceApi;
import com.junoyi.task.domain.vo.ProjectTaskItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目日程业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectScheduleServiceImpl implements IProjectScheduleService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysDictApi sysDictApi;
    private final SysUserMapper sysUserMapper;
    private final TaskServiceApi taskServiceApi;


    /**
     * 获取活跃的项目甘特图列表
     * @param queryDTO 查询参数
     * @return 返回甘特图列表
     */
    @Override
    public List<ProjectGanttVO> getActiveProjectGantList(ProjectGanttQueryDTO queryDTO) {
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
                return new ArrayList<>();
            }

            accessibleProjectIds = userProjects.stream()
                    .map(ProjectMember::getProjectId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 构建查询条件 - 查询活跃的项目（状态为1表示进行中）
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::isDelFlag, false) // 未删除
                // 只查询状态待启动、进行中、暂停、待验收、长期维护的项目，如果是长期维护项目返回数据，但是前端甘特图默认不显示
                .in(Project::getStatus, 0,1,2,3,6)
                .like(StringUtils.isNotBlank(queryDTO.getProjectTitle()), Project::getName, queryDTO.getProjectTitle())
                .eq(queryDTO.getLeader() != null, Project::getLeader, queryDTO.getLeader())
                .orderByAsc(Project::getPlanStartTime); // 按计划开始时间升序排列

        // 如果没有查看所有项目的权限，添加项目ID过滤条件
        if (!hasAllDataPermission && accessibleProjectIds != null) {
            wrapper.in(Project::getId, accessibleProjectIds);
        }

        // 查询项目列表
        List<Project> projects = projectMapper.selectList(wrapper);

        // 如果没有数据，直接返回空结果
        if (projects.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集项目ID
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());

        // 批量查询负责人信息
        List<Long> leaderIds = projects.stream()
                .map(Project::getLeader)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> leaderNameMap = new HashMap<>();
        if (!leaderIds.isEmpty()) {
            leaderNameMap = sysUserMapper.selectBatchIds(leaderIds).stream()
                    .collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName));
        }

        // 批量查询项目任务统计（用于计算完成率）
        Map<Long, ProjectCompletionInfo> completionInfoMap = calculateProjectCompletion(projectIds);

        // 批量查询字典数据
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
        List<ProjectGanttVO> voList = new ArrayList<>();
        Date currentDate = new Date();

        for (Project project : projects) {
            ProjectGanttVO vo = new ProjectGanttVO();

            // 基本信息
            vo.setProjectId(project.getId());
            vo.setProjectNo(project.getNo());
            vo.setProjectTitle(project.getName());
            vo.setStatus(project.getStatus());
            vo.setType(project.getType());
            vo.setPriority(project.getPriority());
            vo.setLeader(project.getLeader());
            vo.setPlanStartTime(project.getPlanStartTime());
            vo.setPlanEndTime(project.getPlanEndTime());

            // 设置负责人名称
            vo.setLeaderName(leaderNameMap.getOrDefault(project.getLeader(), "未知"));

            // 设置完成率
            ProjectCompletionInfo completionInfo = completionInfoMap.get(project.getId());
            if (completionInfo != null) {
                vo.setCompletionRate(completionInfo.getCompletionRate());
            } else {
                vo.setCompletionRate(BigDecimal.ZERO);
            }

            // 判断是否逾期：当前时间超过计划结束时间且项目未完成
            boolean isOverdue = false;
            if (project.getPlanEndTime() != null && currentDate.after(project.getPlanEndTime())) {
                isOverdue = true;
            }
            vo.setOverdue(isOverdue);

            // 字典翻译 - 项目类型
            if (project.getType() != null) {
                SysDictDataVO typeDict = projectTypeDictMap.get(String.valueOf(project.getType()));
                if (typeDict != null) {
                    vo.setTypeLabel(typeDict.getDictLabel());
                    vo.setTypeLabelType(typeDict.getListClass());
                }
            }

            // 字典翻译 - 项目状态
            if (project.getStatus() != null) {
                SysDictDataVO statusDict = projectStatusDictMap.get(String.valueOf(project.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            // 字典翻译 - 项目优先级
            if (project.getPriority() != null) {
                SysDictDataVO priorityDict = projectPriorityDictMap.get(String.valueOf(project.getPriority()));
                if (priorityDict != null) {
                    vo.setPriorityLabel(priorityDict.getDictLabel());
                    vo.setPriorityType(priorityDict.getListClass());
                }
            }

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 批量计算项目完成率信息
     * @param projectIds 项目ID列表
     * @return 项目完成率信息Map，key为项目ID，value为完成率信息
     */
    private Map<Long, ProjectCompletionInfo> calculateProjectCompletion(List<Long> projectIds) {
        Map<Long, ProjectCompletionInfo> completionInfoMap = new HashMap<>();

        for (Long projectId : projectIds) {
            // 获取项目任务列表
            List<ProjectTaskItemVO> taskList = taskServiceApi.getProjectTaskList(projectId);

            // 统计任务数量
            int totalTasks = taskList.size();
            int completedTasks = 0;

            // 统计已完成任务数量（状态为4表示已完成）
            for (ProjectTaskItemVO task : taskList) {
                if (task.getStatus() != null && task.getStatus() == 4) {
                    completedTasks++;
                }
            }

            // 计算完成率（保留两位小数）
            BigDecimal completionRate = BigDecimal.ZERO;
            if (totalTasks > 0) {
                completionRate = BigDecimal.valueOf(completedTasks)
                        .divide(BigDecimal.valueOf(totalTasks), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            ProjectCompletionInfo completionInfo = new ProjectCompletionInfo();
            completionInfo.setCompletionRate(completionRate);

            completionInfoMap.put(projectId, completionInfo);
        }

        return completionInfoMap;
    }

    /**
     * 项目完成率信息内部类
     */
    private static class ProjectCompletionInfo {
        private BigDecimal completionRate;

        public BigDecimal getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(BigDecimal completionRate) {
            this.completionRate = completionRate;
        }
    }
}