package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.project.convert.ProjectMemberConverter;
import com.junoyi.project.domain.dto.ProjectMemberAddDTO;
import com.junoyi.project.domain.dto.ProjectMemberUpdateRoleDTO;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectMemberService;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目成员业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;


    /**
     * 获取项目成员列表
     *
     * @param projectId 项目ID
     * @return 项目成员列表
     */
    @Override
    public List<ProjectMemberVO> getMemberList(Long projectId) {
        // 查询项目成员
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getStatus, 1); // 只查询在职成员
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

        // 使用转换器转换为VO并填充用户信息
        return ProjectMemberConverter.toVOListWithUserInfo(members, userMap);
    }

    /**
     * 获取项目成员下拉选项（支持昵称模糊搜索）
     *
     * @param projectId 项目ID
     * @param nickName 昵称（可选，支持模糊搜索）
     * @return 项目成员用户列表
     */
    @Override
    public List<SysUser> getMemberOptions(Long projectId, String nickName) {
        // 查询项目成员
        LambdaQueryWrapper<ProjectMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getStatus, 1); // 只查询在职成员
        List<ProjectMember> members = projectMemberMapper.selectList(memberWrapper);

        if (members.isEmpty()) {
            return List.of();
        }

        // 获取用户ID列表
        List<Long> userIds = members.stream()
                .map(ProjectMember::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 查询用户信息（支持昵称模糊搜索）
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(SysUser::getUserId, userIds)
                .eq(SysUser::isDelFlag, 0); // 只查询未删除的用户

        // 如果提供了昵称，进行模糊搜索
        if (nickName != null && !nickName.trim().isEmpty()) {
            userWrapper.like(SysUser::getNickName, nickName.trim());
        }

        List<SysUser> users = sysUserMapper.selectList(userWrapper);

        // 清除敏感信息
        users.forEach(user -> {
            user.setPassword(null);
            user.setSalt(null);
        });

        return users;
    }

    /**
     * 添加项目成员
     *
     * @param dto 添加成员DTO
     */
    @Override
    public void addMember(ProjectMemberAddDTO dto) {
        SysUser user = sysUserMapper.selectById(dto.getUserId());
        if (user == null)
            throw new RuntimeException("用户不存在");

        // 验证角色是否有效
        String[] validRoles = {"owner", "admin", "member", "viewer"};
        boolean isValidRole = false;
        for (String validRole : validRoles) {
            if (validRole.equals(dto.getRole())) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole)
            throw new RuntimeException("无效的角色");

        // 检查用户是否已经是项目成员（包括在职和离职）
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, dto.getProjectId())
                .eq(ProjectMember::getUserId, dto.getUserId());
        ProjectMember existingMember = projectMemberMapper.selectOne(wrapper);

        if (existingMember != null) {
            // 如果已存在记录
            if (existingMember.getStatus() == 1) {
                // 在职状态，不允许重复添加
                throw new RuntimeException("该用户已经是项目成员");
            } else {
                // 离职状态，重新激活
                existingMember.setRole(dto.getRole());
                existingMember.setStatus(1);
                existingMember.setJoinTime(new Date());
                existingMember.setLeaveTime(null);
                existingMember.setUpdateTime(new Date());
                projectMemberMapper.updateById(existingMember);
            }
        } else {
            // 不存在记录，创建新成员
            ProjectMember member = new ProjectMember();
            member.setProjectId(dto.getProjectId());
            member.setUserId(dto.getUserId());
            member.setRole(dto.getRole());
            member.setStatus(1);
            member.setJoinTime(new Date());
            member.setCreateTime(new Date());
            member.setUpdateTime(new Date());
            projectMemberMapper.insert(member);
        }
    }

    /**
     * 更新成员角色
     *
     * @param dto 更新角色DTO
     */
    @Override
    public void updateMemberRole(ProjectMemberUpdateRoleDTO dto) {
        // 查询成员记录
        LambdaQueryWrapper<ProjectMember> projectMemberWrapper = new LambdaQueryWrapper<>();
        projectMemberWrapper.eq(ProjectMember::getUserId, dto.getMemberId())
                .eq(ProjectMember::getProjectId, dto.getProjectId())
                .eq(ProjectMember::getStatus, 1);

        ProjectMember member = projectMemberMapper.selectOne(projectMemberWrapper);
        if (member == null)
            throw new RuntimeException("成员不存在");

        // 验证角色是否有效
        String[] validRoles = {"owner", "admin", "member", "viewer"};
        boolean isValidRole = false;
        for (String validRole : validRoles) {
            if (validRole.equals(dto.getRole())) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole)
            throw new RuntimeException("无效的角色");

        // 更新角色
        member.setRole(dto.getRole());
        member.setUpdateTime(new Date());
        projectMemberMapper.updateById(member);
    }

    /**
     * 移除项目成员
     * @param projectId 项目ID
     * @param memberId 成员ID
     */
    @Override
    public void removeMember(Long projectId, Long memberId) {
        // 查询成员记录
        LambdaQueryWrapper<ProjectMember> projectMemberWrapper = new LambdaQueryWrapper<>();
        projectMemberWrapper.eq(ProjectMember::getUserId, memberId)
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getStatus, 1);

        ProjectMember member = projectMemberMapper.selectOne(projectMemberWrapper);
        if (member == null)
            throw new RuntimeException("成员不存在");


        // 软删除：更新状态为离职
        member.setStatus(0);
        member.setLeaveTime(new Date());
        member.setUpdateTime(new Date());
        projectMemberMapper.updateById(member);
    }
}