package com.junoyi.system.event;

import com.junoyi.framework.event.domain.BaseEvent;
import lombok.Getter;

import java.util.Set;

/**
 * 权限变更事件
 * 当权限组、角色权限等发生变更时触发此事件
 *
 * @author Fan
 */
@Getter
public class PermissionChangedEvent extends BaseEvent {

    /**
     * 变更类型
     */
    private final ChangeType changeType;

    /**
     * 变更的资源ID（权限组ID、角色ID等）
     */
    private final Long resourceId;

    /**
     * 受影响的用户ID集合（可选，为空时需要根据 resourceId 查询）
     */
    private final Set<Long> affectedUserIds;

    /**
     * 变更类型枚举
     */
    public enum ChangeType {
        /**
         * 权限组更新
         */
        PERM_GROUP_UPDATE,
        /**
         * 权限组删除
         */
        PERM_GROUP_DELETE,
        /**
         * 角色权限更新
         */
        ROLE_PERM_UPDATE,
        /**
         * 角色删除
         */
        ROLE_DELETE,
        /**
         * 用户角色变更
         */
        USER_ROLE_CHANGE,
        /**
         * 用户权限组变更
         */
        USER_GROUP_CHANGE,
        /**
         * 用户部门变更
         */
        USER_DEPT_CHANGE,
        /**
         * 部门权限组变更
         */
        DEPT_GROUP_CHANGE,
        /**
         * 用户独立权限变更
         */
        USER_PERM_CHANGE
    }

    /**
     * 构造函数 - 指定受影响的用户
     */
    public PermissionChangedEvent(ChangeType changeType, Long resourceId, Set<Long> affectedUserIds) {
        super();
        this.changeType = changeType;
        this.resourceId = resourceId;
        this.affectedUserIds = affectedUserIds;
    }

    /**
     * 构造函数 - 不指定用户，需要后续查询
     */
    public PermissionChangedEvent(ChangeType changeType, Long resourceId) {
        this(changeType, resourceId, null);
    }
}
