package com.junoyi.project.service;

import com.junoyi.project.domain.dto.ProjectMemberAddDTO;
import com.junoyi.project.domain.dto.ProjectMemberUpdateRoleDTO;
import com.junoyi.project.domain.vo.ProjectMemberVO;

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
