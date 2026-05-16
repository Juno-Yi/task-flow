package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.convert.ProjectConverter;
import com.junoyi.project.domain.dto.ProjectListDTO;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.exception.ProjectNotFoundException;
import com.junoyi.project.exception.ProjectPasswordWrongException;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectListService;
import com.junoyi.project.util.ProjectNoGenerateUtil;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目列表业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectListServiceImpl implements IProjectListService {

    private final ProjectListMapper projectListMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;
    private final SysDictApi sysDictApi;

    /**
     * 查询项目列表（分页）
     * @param queryDTO 查询仓鼠
     * @param page 分页参数
     * @return 项目分页结果
     */
    @Override
    public PageResult<ProjectListVO> getProjectList(ProjectListQueryDTO queryDTO, Page<Project> page) {
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
                .eq(queryDTO.getStatus() != null, Project::getStatus, queryDTO.getStatus())
                .eq(Project::isDelFlag, false);

        // 如果没有查看所有项目的权限，添加项目ID过滤条件
        if (!hasAllDataPermission && accessibleProjectIds != null) {
            wrapper.in(Project::getId, accessibleProjectIds);
        }

        wrapper.orderByDesc(Project::getCreateTime);

        // 分页查询项目列表
        Page<Project> resultPage = projectListMapper.selectPage(page, wrapper);
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

        // 收集项目ID和负责人ID
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());
        List<Long> leaderIds = projects.stream().map(Project::getLeader).distinct().collect(Collectors.toList());

        // 批量查询项目成员数量
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.in(ProjectMember::getProjectId, projectIds)
                .eq(ProjectMember::getStatus, 1); // 只统计在职成员
        List<ProjectMember> members = projectMemberMapper.selectList(memberWrapper);


        // 按项目ID分组统计成员数量
        Map<Long, Long> memberCountMap = members.stream()
                .collect(Collectors.groupingBy(ProjectMember::getProjectId, Collectors.counting()));

        // 批量查询负责人信息
        Map<Long, String> leaderNameMap = sysUserMapper.selectBatchIds(leaderIds).stream()
                .collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName));

        // 批量查询项目任务统计
//        Map<Long, TaskStatistics> taskStatisticsMap = calculateTaskStatistics(projectIds);

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
//            TaskStatistics taskStats = taskStatisticsMap.get(project.getId());
//            if (taskStats != null) {
//                vo.setTotalTasks(taskStats.getTotalTasks());
//                vo.setCompletedTasks(taskStats.getCompletedTasks());
//                vo.setProgress(taskStats.getProgress());
//            } else {
//                vo.setTotalTasks(0);
//                vo.setCompletedTasks(0);
//                vo.setProgress(0);
//            }

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
     * 添加项目
     * @param dto 项目传输数据
     */
    @Override
    public void addProject(ProjectListDTO dto) {
        Project project = ProjectConverter.toPo(dto);

        if (dto.getPriority() == null) {
            project.setPriority(0);
        }

        // 默认状态为0（规划中）
        if (dto.getStatus() == null) {
            project.setStatus(0);
        }

        project.setDelFlag(false);
        project.setCreateBy(SecurityUtils.getUserName());
        project.setCreateTime(DateUtils.getNowDate());

        // 生成项目编号并插入，如果编号重复则重试（最多3次）
        int maxRetries = 3;
        int retryCount = 0;
        String projectNo = null;

        while (retryCount < maxRetries) {
            try {
                // 生成项目编号
                projectNo = ProjectNoGenerateUtil.generateProjectCode();
                project.setNo(projectNo);

                // 尝试插入项目
                projectListMapper.insert(project);

                // 插入成功，跳出循环
                break;
            } catch (Exception e) {
                retryCount++;

                // 检查是否是唯一索引冲突
                if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                    if (retryCount < maxRetries) {
                        // 重试前等待一小段时间（避免立即重试导致再次冲突）
                        try {
                            Thread.sleep(50 * retryCount); // 递增等待时间：50ms, 100ms, 150ms
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }

                // 如果不是唯一索引冲突，或者已达到最大重试次数，抛出异常
                throw new RuntimeException("创建项目失败：" + e.getMessage(), e);
            }
        }

        if (projectNo == null) {
            throw new RuntimeException("创建项目失败：无法生成唯一的项目编号");
        }

        // 自动将项目负责人添加到项目成员中，角色为 owner
        ProjectMember leaderMember = new ProjectMember();
        leaderMember.setProjectId(project.getId());
        leaderMember.setUserId(project.getLeader());
        leaderMember.setRole("owner");
        leaderMember.setStatus(1);
        leaderMember.setJoinTime(new Date());
        leaderMember.setCreateTime(new Date());
        leaderMember.setUpdateTime(new Date());
        projectMemberMapper.insert(leaderMember);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("create", "project",
                "创建了项目「" + project.getName() + "」（编号：" + projectNo + "）",
                String.valueOf(project.getId()), project.getName(),
                JsonUtils.toJsonString(dto)
        ));
    }

    /**
     * 修改项目
     * @param dto 项目传输数据
     */
    @Override
    public void updateProject(ProjectListDTO dto) {
        // 检查项目ID是否存在
        if (dto.getId() == null) {
            throw new RuntimeException("项目ID不能为空");
        }

        // 查询原项目信息
        Project existingProject = projectListMapper.selectById(dto.getId());
        if (existingProject == null) {
            throw new RuntimeException("项目不存在");
        }

        // 转换DTO为PO
        Project project = ProjectConverter.toPo(dto);

        // 保留原有的项目编号和创建信息
        project.setNo(existingProject.getNo());
        project.setCreateBy(existingProject.getCreateBy());
        project.setCreateTime(existingProject.getCreateTime());

        // 设置更新信息
        project.setUpdateBy(SecurityUtils.getUserName());
        project.setUpdateTime(DateUtils.getNowDate());

        // 更新项目
        projectListMapper.updateById(project);

        // 如果项目负责人发生变化，需要更新成员表
        if (!existingProject.getLeader().equals(project.getLeader())) {
            // 检查新负责人是否已经是项目成员
            LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                    .eq(ProjectMember::getUserId, project.getLeader());
            ProjectMember existingMember = projectMemberMapper.selectOne(memberWrapper);

            if (existingMember == null) {
                // 新负责人不是项目成员，添加为成员
                ProjectMember newLeaderMember = new ProjectMember();
                newLeaderMember.setProjectId(project.getId());
                newLeaderMember.setUserId(project.getLeader());
                newLeaderMember.setRole("owner");
                newLeaderMember.setStatus(1);
                newLeaderMember.setJoinTime(new Date());
                newLeaderMember.setCreateTime(new Date());
                newLeaderMember.setUpdateTime(new Date());
                projectMemberMapper.insert(newLeaderMember);
            } else {
                // 新负责人已是项目成员，更新角色为owner
                existingMember.setRole("owner");
                existingMember.setUpdateTime(new Date());
                projectMemberMapper.updateById(existingMember);
            }

            // 将原负责人的角色从owner改为member（如果存在）
            LambdaQueryWrapper<ProjectMember> oldLeaderWrapper = new LambdaQueryWrapper<>();
            oldLeaderWrapper.eq(ProjectMember::getProjectId, project.getId())
                    .eq(ProjectMember::getUserId, existingProject.getLeader())
                    .eq(ProjectMember::getRole, "owner");
            ProjectMember oldLeaderMember = projectMemberMapper.selectOne(oldLeaderWrapper);
            if (oldLeaderMember != null) {
                oldLeaderMember.setRole("member");
                oldLeaderMember.setUpdateTime(new Date());
                projectMemberMapper.updateById(oldLeaderMember);
            }
        }

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("update", "project",
                "修改了项目「" + project.getName() + "」（编号：" + project.getNo() + "）",
                String.valueOf(project.getId()), project.getName(),
                JsonUtils.toJsonString(dto)
        ));
    }

    /**
     * 验证当前用户密码
     * @param password 密码
     */
    private void verifyCurrentUserPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new ProjectPasswordWrongException("密码不能为空");
        }

        // 获取当前登录用户名
        String currentUsername = SecurityUtils.getUserName();
        if (StringUtils.isBlank(currentUsername)) {
            throw new ProjectPasswordWrongException("未获取到当前登录用户信息");
        }

        // 查询当前用户
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUserName, currentUsername);
        SysUser user = sysUserMapper.selectOne(userWrapper);

        if (user == null) {
            throw new ProjectPasswordWrongException("用户不存在");
        }

        // 验证密码
        if (!PasswordUtils.matches(password, user.getSalt(), user.getPassword())) {
            throw new ProjectPasswordWrongException("密码错误");
        }
    }


    /**
     * 删除项目（软删除，需要密码验证）
     * @param id 项目ID
     * @param password 密码
     */
    @Override
    public void deleteProjectRepo(Long id, String password) {
        // 验证当前用户密码
        verifyCurrentUserPassword(password);

        // 查询项目
        Project project = projectListMapper.selectById(id);
        if (project == null || project.isDelFlag()) {
            throw new ProjectNotFoundException("项目不存在");
        }

        // 软删除
        project.setDelFlag(true);
        project.setUpdateBy(SecurityUtils.getUserName());
        project.setUpdateTime(new Date());
        projectListMapper.updateById(project);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("delete", "project",
                "删除了项目「" + project.getName() + "」（编号：" + project.getNo() + "）",
                String.valueOf(project.getId()), project.getName(),
                null
        ));
    }

    /**
     * 批量删除项目（软删除，需要密码验证）
     * @param ids 项目ID列表
     * @param password 密码
     */
    @Override
    public void deleteProjectRepoBatch(List<Long> ids, String password) {
        if (ids == null || ids.isEmpty()) {
            throw new ProjectNotFoundException("项目ID列表不能为空");
        }

        // 验证当前用户密码
        verifyCurrentUserPassword(password);

        // 批量查询项目
        List<Project> projects = projectListMapper.selectBatchIds(ids);
        if (projects.isEmpty()) {
            throw new ProjectNotFoundException("未找到要删除的项目");
        }

        // 批量软删除
        String currentUser = SecurityUtils.getUserName();
        Date now = new Date();

        for (Project project : projects) {
            if (!project.isDelFlag()) {
                project.setDelFlag(true);
                project.setUpdateBy(currentUser);
                project.setUpdateTime(now);
                projectListMapper.updateById(project);

                // 发布操作日志事件
                EventBus.get().callEvent(UserOperationEvent.withRawData("delete", "project",
                        "删除了项目「" + project.getName() + "」（编号：" + project.getNo() + "）",
                        String.valueOf(project.getId()), project.getName(),
                        null
                ));
            }
        }
    }



}