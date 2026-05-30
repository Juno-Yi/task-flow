package com.junoyi.project.service;

import com.junoyi.project.domain.dto.ProjectMemberAddDTO;
import com.junoyi.project.domain.dto.ProjectMemberUpdateRoleDTO;
import com.junoyi.project.domain.vo.ProjectMemberVO;
import com.junoyi.system.domain.po.SysUser;

import java.util.List;

/**
 * 项目成员业务接口
 *
 * @author Fan
 */
public interface IProjectMemberService {

    /**
     * 获取项目成员列表
     *
     * @param projectId 项目ID
     * @return 项目成员列表
     */
    List<ProjectMemberVO> getMemberList(Long projectId);

    /**
     * 获取项目成员下拉选项（支持昵称模糊搜索）
     *
     * @param projectId 项目ID
     * @param nickName 昵称（可选，支持模糊搜索）
     * @return 项目成员用户列表
     */
    List<SysUser> getMemberOptions(Long projectId, String nickName);

    /**
     * 添加项目成员
     *
     * @param dto 添加成员DTO
     */
    void addMember(ProjectMemberAddDTO dto);

    /**
     * 更新成员角色
     *
     * @param dto 更新角色DTO
     */
    void updateMemberRole(ProjectMemberUpdateRoleDTO dto);

    /**
     * 移除项目成员
     * @param projectId 项目ID
     * @param memberId 成员ID
     */
    void removeMember(Long projectId,Long memberId);

}
