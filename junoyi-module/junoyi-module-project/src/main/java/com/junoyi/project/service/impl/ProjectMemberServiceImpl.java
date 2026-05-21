package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.project.convert.ProjectMemberConverter;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectMemberService;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}