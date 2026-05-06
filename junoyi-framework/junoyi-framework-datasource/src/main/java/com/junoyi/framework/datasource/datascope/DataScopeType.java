package com.junoyi.framework.datasource.datascope;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据范围类型枚举
 * <p>
 * 优先级顺序：ALL > DEPT_AND_CHILD > DEPT > SELF
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

    /**
     * 全部数据权限
     */
    ALL("1", "全部数据", 1),

    /**
     * 本部门数据权限
     */
    DEPT("2", "本部门数据", 3),

    /**
     * 本部门及下级部门数据权限
     */
    DEPT_AND_CHILD("3", "本部门及下级数据", 2),

    /**
     * 仅本人数据权限
     */
    SELF("4", "仅本人数据", 4);

    /**
     * 数据范围值（存储在数据库中）
     */
    private final String value;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 优先级（数值越小优先级越高）
     */
    private final int priority;

    /**
     * 根据值获取枚举
     */
    public static DataScopeType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataScopeType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 比较两个数据范围，返回权限更大的那个（用于多角色取并集）
     */
    public static DataScopeType max(DataScopeType a, DataScopeType b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.getPriority() <= b.getPriority() ? a : b;
    }
}
