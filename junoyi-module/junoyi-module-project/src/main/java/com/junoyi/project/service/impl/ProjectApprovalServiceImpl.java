package com.junoyi.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.project.domain.dto.ProjectListQueryDTO;
import com.junoyi.project.domain.po.Project;
import com.junoyi.project.domain.po.ProjectMember;
import com.junoyi.project.domain.vo.ProjectListVO;
import com.junoyi.project.mapper.ProjectApprovalMapper;
import com.junoyi.project.mapper.ProjectMemberMapper;
import com.junoyi.project.service.IProjectApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目立项业务接口
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class ProjectApprovalServiceImpl implements IProjectApprovalService {

    private final ProjectApprovalMapper projectApprovalMapper;
    private final ProjectMemberMapper projectMemberMapper;

    /**
     * 获取立项项目列表
     * @param queryDTO 查询参数
     * @return 立项项目列表
     */
    @Override
    public PageResult<ProjectListVO> getApprovalList(ProjectListQueryDTO queryDTO, Page<Project> page) {
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
        return null;
    }
}