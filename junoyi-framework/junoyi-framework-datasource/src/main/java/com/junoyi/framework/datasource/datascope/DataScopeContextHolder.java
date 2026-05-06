package com.junoyi.framework.datasource.datascope;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 数据范围上下文持有者
 * <p>
 * 使用 ThreadLocal 存储当前请求的数据范围信息
 * 由上层模块（如 security）在请求进入时设置，请求结束时清理
 *
 * @author Fan
 */
public class DataScopeContextHolder {

    private static final ThreadLocal<DataScopeContext> CONTEXT = new ThreadLocal<>();

    /**
     * 设置数据范围上下文
     */
    public static void set(DataScopeContext context) {
        CONTEXT.set(context);
    }

    /**
     * 获取数据范围上下文
     */
    public static DataScopeContext get() {
        return CONTEXT.get();
    }

    /**
     * 清理数据范围上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 数据范围上下文
     */
    @Data
    @Builder
    public static class DataScopeContext {

        /**
         * 当前用户ID
         */
        private Long userId;

        /**
         * 当前用户名（用于 SELF 模式，create_by 字段通常存储用户名）
         */
        private String userName;

        /**
         * 当前用户所属部门ID集合
         */
        private Set<Long> deptIds;

        /**
         * 数据范围类型
         */
        private DataScopeType scopeType;

        /**
         * 可访问的部门ID集合（包含下级部门，用于 DEPT_AND_CHILD）
         */
        private Set<Long> accessibleDeptIds;

        /**
         * 是否为超级管理员
         */
        private boolean superAdmin;
    }
}
