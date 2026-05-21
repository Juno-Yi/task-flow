package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.exception.ProjectNotFoundException;
import com.junoyi.project.mapper.ProjectMapper;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 项目权限业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectPermissionServiceImpl implements IProjectPermissionService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;

    /**
     * 判断用户是否能查看项目详情的权限
     * @param projectNo 项目编号
     * @param userId 用户ID
     * @return 如果有权限返回true，没有权限就返回false
     */
    @Override
    public boolean hasProjectViewPermission(String projectNo, Long userId) {
        // 如果用户有 project.data.list.all权限就跳过
        if (PermissionHelper.hasPermission("project.data.list.all"))
            return true;

        // 查询项目是否存在
        LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
        projectWrapper.eq(projectNo != null, Project::getNo, projectNo)
                .eq(Project::isDelFlag,false);
        Project project = projectMapper.selectOne(projectWrapper);
        if (project == null)
            throw new ProjectNotFoundException("该项目不存在！");

        // 去判断用户在不在项目成员中
        // 判断是否为项目成员
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                .eq(ProjectMember::getUserId, userId);

        Long count = projectMemberMapper.selectCount(memberWrapper);

        return count > 0;
    }

    /**
     * 判断用户是否能查看项目详情数据的权限
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 如果有权限返回true，没有权限就返回false
     */
    @Override
    public boolean hasProjectViewPermission(Long projectId, Long userId) {
        if (PermissionHelper.hasPermission("project.data.list.all"))
            return true;

        LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
        projectWrapper.eq(projectId != null, Project::getId, projectId)
                .eq(Project::isDelFlag,false);
        Project project = projectMapper.selectOne(projectWrapper);
        if (project == null)
            throw new ProjectNotFoundException("该项目不存在！");
        // 去判断用户在不在项目成员中
        // 判断是否为项目成员
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                .eq(ProjectMember::getUserId, userId);

        Long count = projectMemberMapper.selectCount(memberWrapper);
        return count > 0;
    }
}