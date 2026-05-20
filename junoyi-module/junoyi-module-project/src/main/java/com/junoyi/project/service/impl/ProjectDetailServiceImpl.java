package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectDetailVO;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.exception.ProjectNotFoundException;
import com.junoyi.project.mapper.ProjectMapper;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectDetailService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目详情业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectDetailServiceImpl implements IProjectDetailService {

    private final ProjectMapper projectMapper;
    private final SysUserMapper sysUserMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysDictApi sysDictApi;

    /**
     * 通过项目编号获取项目详情
     * @param projectNo 项目编号
     * @return 项目详情
     */
    @Override
    public ProjectDetailVO getProjectDetailByNo(String projectNo) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getNo, projectNo)
                .eq(Project::isDelFlag, false);
        Project project = projectMapper.selectOne(wrapper);

        if (project == null)
            throw new ProjectNotFoundException("项目不存在");

        return buildProjectDetail(project);
    }

    /**
     * 构建项目详情
     * @param project 项目
     * @return 项目详情
     */
    private ProjectDetailVO buildProjectDetail(Project project){
        ProjectDetailVO detailVO = new ProjectDetailVO();
        BeanUtils.copyProperties(project, detailVO);

        // 查询负责人信息
        SysUser leader = sysUserMapper.selectById(project.getLeader());
        if (leader != null)
            detailVO.setLeaderName(leader.getNickName());

        // 统计项目成员数量
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ProjectMember::getProjectId, project.getId())
                .eq(ProjectMember::getStatus, 1);
        long memberCount = projectMemberMapper.selectCount(memberWrapper);
        detailVO.setMemberCount((int) memberCount);

        // TODO 统计项目进度
        detailVO.setTotalTasks(0);
        detailVO.setCompletedTasks(0);
        detailVO.setPendingTasks(0);
        detailVO.setProgress(0);

        // TODO: 统计仓库数量
        detailVO.setRepositoryCount(0);

        // TODO: 统计文档数量
        detailVO.setDocumentCount(0);

        // TODO: 统计里程碑数量
        detailVO.setMilestoneCount(0);

        // 字典翻译 - 项目类型
        if (project.getType() != null) {
            SysDictDataVO typeDict = sysDictApi.getDictItem("project_type", String.valueOf(project.getType()));
            if (typeDict != null) {
                detailVO.setTypeLabel(typeDict.getDictLabel());
                detailVO.setTypeLabelType(typeDict.getListClass());
            }
        }

        // 字典翻译 - 项目状态
        if (project.getStatus() != null) {
            SysDictDataVO statusDict = sysDictApi.getDictItem("project_status", String.valueOf(project.getStatus()));
            if (statusDict != null) {
                detailVO.setStatusLabel(statusDict.getDictLabel());
                detailVO.setStatusType(statusDict.getListClass());
            }
        }

        // 字典翻译 - 项目优先级
        if (project.getPriority() != null) {
            SysDictDataVO priorityDict = sysDictApi.getDictItem("project_priority", String.valueOf(project.getPriority()));
            if (priorityDict != null) {
                detailVO.setPriorityLabel(priorityDict.getDictLabel());
                detailVO.setPriorityType(priorityDict.getListClass());
            }
        }

        // 查询最近的项目成员（最多5个，用于概览页面展示）
        detailVO.setRecentMembers(getRecentMembers(project.getId(), 5));

        // 查询当前用户在项目中的角色
        Long currentUserId = SecurityUtils.getUserId();
        LambdaQueryWrapper<ProjectMember> currentUserWrapper = new LambdaQueryWrapper<>();
        currentUserWrapper.eq(ProjectMember::getProjectId, project.getId())
                .eq(ProjectMember::getUserId, currentUserId)
                .eq(ProjectMember::getStatus, 1);
        ProjectMember currentUserMember = projectMemberMapper.selectOne(currentUserWrapper);
        if (currentUserMember != null) {
            detailVO.setCurrentUserRole(currentUserMember.getRole());
        } else {
            // 如果用户不是项目成员，设置为 null
            detailVO.setCurrentUserRole(null);
        }
        return detailVO;
    }

    /**
     * 获取最近的项目成员
     * @param projectId 项目ID
     * @param limit 限制数量
     * @return 成员列表
     */
    private List<ProjectMemberVO> getRecentMembers(Long projectId, int limit) {
        // 查询项目成员（按加入时间倒序，只查询在职成员）
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getStatus, 1)
                .orderByDesc(ProjectMember::getJoinTime)
                .last("LIMIT " + limit);
        List<ProjectMember> members = projectMemberMapper.selectList(wrapper);

        if (members.isEmpty()) {
            return List.of();
        }

        // 获取用户ID列表
        List<Long> userIds = members.stream()
                .map(ProjectMember::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getUserId, user -> user));

        // 转换为VO
        return members.stream()
                .map(member -> {
                    ProjectMemberVO vo = new ProjectMemberVO();
                    vo.setId(member.getId());
                    vo.setProjectId(member.getProjectId());
                    vo.setUserId(member.getUserId());
                    vo.setRole(member.getRole());
                    vo.setStatus(member.getStatus());
                    vo.setJoinTime(member.getJoinTime());

                    // 填充用户信息
                    SysUser user = userMap.get(member.getUserId());
                    if (user != null) {
                        vo.setUserName(user.getUserName());
                        vo.setNickName(user.getNickName());
                        vo.setAvatar(user.getAvatar());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }
}