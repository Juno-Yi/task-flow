package com.junoyi.project.convert;

import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.system.domain.po.SysUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目成员转换器
 *
 * @author Fan
 */
public final class ProjectMemberConverter {

    private ProjectMemberConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将 ProjectMember 实体对象转换成 ProjectMemberVO 对象
     * @param entity ProjectMember实体对象
     * @return ProjectMemberVO 实体对象
     */
    public static ProjectMemberVO toVO(ProjectMember entity){
        ProjectMemberVO vo = new ProjectMemberVO();
        vo.setId(entity.getId());
        vo.setProjectId(entity.getProjectId());
        vo.setUserId(entity.getUserId());
        vo.setRole(entity.getRole());
        vo.setStatus(entity.getStatus());
        vo.setJoinTime(entity.getJoinTime());
        vo.setLeaveTime(entity.getLeaveTime());
        return vo;
    }

    /**
     * 批量转换 ProjectMember 列表为 ProjectMemberVO 列表，并填充用户信息
     *
     * @param members 项目成员列表
     * @param userMap 用户信息Map（key: userId, value: SysUser）
     * @return ProjectMemberVO 列表
     */
    public static List<ProjectMemberVO> toVOListWithUserInfo(List<ProjectMember> members, Map<Long, SysUser> userMap) {
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return members.stream()
                .map(member -> toVOWithUserInfo(member, userMap))
                .collect(Collectors.toList());
    }

    /**
     * 将 ProjectMember 转换为 ProjectMemberVO，并填充用户信息
     *
     * @param member 项目成员
     * @param userMap 用户信息Map（key: userId, value: SysUser）
     * @return ProjectMemberVO
     */
    public static ProjectMemberVO toVOWithUserInfo(ProjectMember member, Map<Long, SysUser> userMap) {
        ProjectMemberVO vo = toVO(member);

        // 填充用户信息
        SysUser user = userMap.get(member.getUserId());
        if (user != null) {
            vo.setUserName(user.getUserName());
            vo.setNickName(user.getNickName());
            vo.setAvatar(user.getAvatar());
        }

        return vo;
    }
}