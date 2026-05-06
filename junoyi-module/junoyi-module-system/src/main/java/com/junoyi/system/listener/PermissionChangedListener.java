package com.junoyi.system.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.helper.SessionHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.system.domain.po.*;
import com.junoyi.system.event.PermissionChangedEvent;
import com.junoyi.system.mapper.*;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限变更事件监听器
 * 监听权限变更事件，同步更新受影响用户的会话权限
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class PermissionChangedListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(PermissionChangedListener.class);

    private final SessionHelper sessionHelper;
    private final SysUserGroupMapper sysUserGroupMapper;
    private final SysRoleGroupMapper sysRoleGroupMapper;
    private final SysDeptGroupMapper sysDeptGroupMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserPermMapper sysUserPermMapper;
    private final SysPermGroupMapper sysPermGroupMapper;

    /**
     * 处理权限变更事件
     */
    @EventHandler(async = true)
    public void onPermissionChanged(PermissionChangedEvent event) {
        try {
            log.info("PermissionSync", "收到权限变更事件 | 类型: " + event.getChangeType() 
                    + " | 资源ID: " + event.getResourceId());

            Set<Long> affectedUserIds = event.getAffectedUserIds();
            
            // 如果没有指定受影响的用户，根据变更类型查询
            if (affectedUserIds == null || affectedUserIds.isEmpty()) {
                affectedUserIds = findAffectedUsers(event.getChangeType(), event.getResourceId());
            }

            if (affectedUserIds.isEmpty()) {
                log.info("PermissionSync", "没有受影响的在线用户");
                return;
            }

            log.info("PermissionSync", "受影响用户数: " + affectedUserIds.size() 
                    + " | 用户ID列表: " + affectedUserIds);

            // 同步每个用户的会话
            int syncCount = 0;
            int failCount = 0;
            for (Long userId : affectedUserIds) {
                try {
                    if (syncUserSessions(userId)) {
                        syncCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("PermissionSyncError", "同步用户会话失败 | userId: " + userId, e);
                }
            }

            log.info("PermissionSync", "权限同步完成 | 成功: " + syncCount + " | 失败: " + failCount);
        } catch (Exception e) {
            log.error("PermissionSyncFatalError", "权限变更事件处理失败 | 类型: " + event.getChangeType() 
                    + " | 资源ID: " + event.getResourceId(), e);
        }
    }

    /**
     * 根据变更类型查找受影响的用户
     */
    private Set<Long> findAffectedUsers(PermissionChangedEvent.ChangeType changeType, Long resourceId) {
        Set<Long> userIds = new HashSet<>();
        Date now = new Date();

        switch (changeType) {
            case PERM_GROUP_UPDATE, PERM_GROUP_DELETE -> {
                // 权限组变更：查找直接绑定该权限组的用户
                userIds.addAll(findUsersByPermGroup(resourceId, now));
                // 查找通过角色绑定该权限组的用户
                userIds.addAll(findUsersByRoleGroup(resourceId, now));
                // 查找通过部门绑定该权限组的用户
                userIds.addAll(findUsersByDeptGroup(resourceId, now));
            }
            case ROLE_PERM_UPDATE, ROLE_DELETE -> {
                // 角色变更：查找拥有该角色的用户
                userIds.addAll(findUsersByRole(resourceId));
            }
            case USER_ROLE_CHANGE, USER_GROUP_CHANGE, USER_DEPT_CHANGE, USER_PERM_CHANGE -> {
                // 用户角色/权限组/部门/独立权限变更：直接就是该用户
                userIds.add(resourceId);
            }
            case DEPT_GROUP_CHANGE -> {
                // 部门权限组变更：查找该部门下的用户
                userIds.addAll(findUsersByDept(resourceId));
            }
        }

        return userIds;
    }

    /**
     * 查找直接绑定权限组的用户
     */
    private Set<Long> findUsersByPermGroup(Long groupId, Date now) {
        return sysUserGroupMapper.selectList(
                new LambdaQueryWrapper<SysUserGroup>()
                        .select(SysUserGroup::getUserId)
                        .eq(SysUserGroup::getGroupId, groupId)
                        .and(w -> w.isNull(SysUserGroup::getExpireTime).or().gt(SysUserGroup::getExpireTime, now))
        ).stream().map(SysUserGroup::getUserId).collect(Collectors.toSet());
    }

    /**
     * 查找通过角色绑定权限组的用户
     */
    private Set<Long> findUsersByRoleGroup(Long groupId, Date now) {
        // 先找绑定该权限组的角色
        Set<Long> roleIds = sysRoleGroupMapper.selectList(
                new LambdaQueryWrapper<SysRoleGroup>()
                        .select(SysRoleGroup::getRoleId)
                        .eq(SysRoleGroup::getGroupId, groupId)
                        .and(w -> w.isNull(SysRoleGroup::getExpireTime).or().gt(SysRoleGroup::getExpireTime, now))
        ).stream().map(SysRoleGroup::getRoleId).collect(Collectors.toSet());

        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 再找拥有这些角色的用户
        return sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getUserId)
                        .in(SysUserRole::getRoleId, roleIds)
        ).stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
    }

    /**
     * 查找通过部门绑定权限组的用户
     */
    private Set<Long> findUsersByDeptGroup(Long groupId, Date now) {
        // 先找绑定该权限组的部门
        Set<Long> deptIds = sysDeptGroupMapper.selectList(
                new LambdaQueryWrapper<SysDeptGroup>()
                        .select(SysDeptGroup::getDeptId)
                        .eq(SysDeptGroup::getGroupId, groupId)
                        .and(w -> w.isNull(SysDeptGroup::getExpireTime).or().gt(SysDeptGroup::getExpireTime, now))
        ).stream().map(SysDeptGroup::getDeptId).collect(Collectors.toSet());

        if (deptIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 再找这些部门下的用户
        return sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getUserId)
                        .in(SysUserDept::getDeptId, deptIds)
        ).stream().map(SysUserDept::getUserId).collect(Collectors.toSet());
    }

    /**
     * 查找拥有指定角色的用户
     */
    private Set<Long> findUsersByRole(Long roleId) {
        return sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getUserId)
                        .eq(SysUserRole::getRoleId, roleId)
        ).stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
    }

    /**
     * 查找指定部门下的用户
     */
    private Set<Long> findUsersByDept(Long deptId) {
        return sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getUserId)
                        .eq(SysUserDept::getDeptId, deptId)
        ).stream().map(SysUserDept::getUserId).collect(Collectors.toSet());
    }

    /**
     * 同步用户的所有会话权限
     * @return 是否有会话被更新
     */
    private boolean syncUserSessions(Long userId) {
        // 获取用户所有活跃会话
        List<UserSession> sessions = sessionHelper.getUserSessions(userId);
        if (sessions.isEmpty()) {
            log.debug("PermissionSync", "用户无活跃会话 | userId: " + userId);
            return false;
        }

        log.info("PermissionSync", "开始同步用户会话 | userId: " + userId 
                + " | 会话数: " + sessions.size());

        // 重新加载用户权限
        LoginUser updatedLoginUser = reloadUserPermissions(userId, sessions.get(0));
        if (updatedLoginUser == null) {
            log.warn("PermissionSync", "重新加载用户权限失败 | userId: " + userId);
            return false;
        }

        // 更新所有会话
        int successCount = 0;
        for (UserSession session : sessions) {
            try {
                boolean updated = sessionHelper.updateSession(session.getSessionId(), updatedLoginUser);
                if (updated) {
                    successCount++;
                    log.debug("PermissionSync", "会话更新成功 | userId: " + userId 
                            + " | sessionId: " + session.getSessionId().substring(0, 8) + "...");
                } else {
                    log.warn("PermissionSync", "会话更新失败 | userId: " + userId 
                            + " | sessionId: " + session.getSessionId().substring(0, 8) + "...");
                }
            } catch (Exception e) {
                log.error("PermissionSync", "会话更新异常 | userId: " + userId 
                        + " | sessionId: " + session.getSessionId().substring(0, 8) + "...", e);
            }
        }

        log.info("PermissionSync", "用户会话权限已更新 | userId: " + userId 
                + " | 总会话数: " + sessions.size() 
                + " | 成功: " + successCount 
                + " | 失败: " + (sessions.size() - successCount));
        
        return successCount > 0;
    }

    /**
     * 重新加载用户权限
     */
    private LoginUser reloadUserPermissions(Long userId, UserSession existingSession) {
        Date now = new Date();

        // 超级管理员特殊处理
        if (userId == 1L) {
            Set<String> permissions = new HashSet<>();
            permissions.add("*");
            Set<String> groups = new HashSet<>();
            groups.add("super_admin");
            
            return LoginUser.builder()
                    .userId(userId)
                    .userName(existingSession.getUserName())
                    .nickName(existingSession.getNickName())
                    .platformType(existingSession.getPlatformType())
                    .permissions(permissions)
                    .groups(groups)
                    .depts(existingSession.getDepts())
                    .roles(existingSession.getRoles())
                    .superAdmin(true)
                    .loginIp(existingSession.getLoginIp())
                    .loginTime(existingSession.getLoginTime())
                    .build();
        }

        // 查询用户角色
        Set<Long> roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());

        // 查询用户部门
        Set<Long> deptIds = sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getDeptId)
                        .eq(SysUserDept::getUserId, userId)
        ).stream().map(SysUserDept::getDeptId).collect(Collectors.toSet());

        // 收集所有权限组ID
        Set<Long> groupIds = new HashSet<>();

        // 用户直绑权限组
        sysUserGroupMapper.selectList(
                new LambdaQueryWrapper<SysUserGroup>()
                        .select(SysUserGroup::getGroupId)
                        .eq(SysUserGroup::getUserId, userId)
                        .and(w -> w.isNull(SysUserGroup::getExpireTime).or().gt(SysUserGroup::getExpireTime, now))
        ).forEach(ug -> groupIds.add(ug.getGroupId()));

        // 角色绑定权限组
        if (!roleIds.isEmpty()) {
            sysRoleGroupMapper.selectList(
                    new LambdaQueryWrapper<SysRoleGroup>()
                            .select(SysRoleGroup::getGroupId)
                            .in(SysRoleGroup::getRoleId, roleIds)
                            .and(w -> w.isNull(SysRoleGroup::getExpireTime).or().gt(SysRoleGroup::getExpireTime, now))
            ).forEach(rg -> groupIds.add(rg.getGroupId()));
        }

        // 部门绑定权限组
        if (!deptIds.isEmpty()) {
            sysDeptGroupMapper.selectList(
                    new LambdaQueryWrapper<SysDeptGroup>()
                            .select(SysDeptGroup::getGroupId)
                            .in(SysDeptGroup::getDeptId, deptIds)
                            .and(w -> w.isNull(SysDeptGroup::getExpireTime).or().gt(SysDeptGroup::getExpireTime, now))
            ).forEach(dg -> groupIds.add(dg.getGroupId()));
        }

        // 查询权限组详情
        Set<String> groupCodes = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        if (!groupIds.isEmpty()) {
            List<SysPermGroup> groups = sysPermGroupMapper.selectList(
                    new LambdaQueryWrapper<SysPermGroup>()
                            .in(SysPermGroup::getId, groupIds)
                            .eq(SysPermGroup::getStatus, 1)
            );
            for (SysPermGroup group : groups) {
                groupCodes.add(group.getGroupCode());
                if (group.getPermissions() != null) {
                    permissions.addAll(group.getPermissions());
                }
            }
        }

        // 加载用户独立权限
        sysUserPermMapper.selectList(
                new LambdaQueryWrapper<SysUserPerm>()
                        .select(SysUserPerm::getPermission)
                        .eq(SysUserPerm::getUserId, userId)
                        .and(w -> w.isNull(SysUserPerm::getExpireTime).or().gt(SysUserPerm::getExpireTime, now))
        ).forEach(up -> permissions.add(up.getPermission()));

        boolean isSuperAdmin = permissions.contains("*");

        return LoginUser.builder()
                .userId(userId)
                .userName(existingSession.getUserName())
                .nickName(existingSession.getNickName())
                .platformType(existingSession.getPlatformType())
                .permissions(permissions)
                .groups(groupCodes)
                .depts(deptIds)
                .roles(roleIds)
                .superAdmin(isSuperAdmin)
                .loginIp(existingSession.getLoginIp())
                .loginTime(existingSession.getLoginTime())
                .build();
    }
}
