package com.junoyi.project.service;

/**
 * 项目权限业务接口
 *
 * @author Fan
 */
public interface IProjectPermissionService {

    /**
     * 判断用户是否能查看项目详情数据的权限
     * @param projectNo 项目编号
     * @param userId 用户ID
     * @return 如果有权限返回true，没有权限就返回false
     */
    boolean hasProjectViewPermission(String projectNo, Long userId);

    /**
     * 判断用户是否能查看项目详情数据的权限
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 如果有权限返回true，没有权限就返回false
     */
    boolean hasProjectViewPermission(Long projectId, Long userId);

    /**
     * 用户是否为项目负责人
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 如果是就返回true，否则就返回false
     */
    boolean isProjectOwner(Long projectId, Long userId);

    /**
     * 用户是否为项目管理员
     * @param projectId 项目ID
     * @param userId 用户ID
     * @return 如果是就返回true，否则就返回false
     */
    boolean isProjectAdmin(Long projectId, Long userId);
}
