package com.junoyi.framework.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据权限配置属性
 * <p>
 * 用于配置数据范围（DataScope）功能的各项参数
 *
 * @author Fan
 */
@ConfigurationProperties(prefix = "junoyi.data-permission")
public class DataPermissionProperties {

    /**
     * 是否启用数据权限功能
     */
    private boolean enabled = true;

    /**
     * 数据范围配置
     */
    private DataScopeConfig dataScope = new DataScopeConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DataScopeConfig getDataScope() {
        return dataScope;
    }

    public void setDataScope(DataScopeConfig dataScope) {
        this.dataScope = dataScope;
    }

    /**
     * 数据范围配置
     */
    public static class DataScopeConfig {

        /**
         * 工作模式
         * ANNOTATION: 注解模式，仅对标注 @DataScope 的方法生效（默认，推荐）
         * GLOBAL: 全局模式，对所有查询生效（性能开销较大）
         */
        private Mode mode = Mode.ANNOTATION;

        /**
         * 默认部门字段名
         */
        private String deptField = "dept_id";

        /**
         * 默认用户字段名（用于 SELF 模式）
         */
        private String userField = "create_by";

        /**
         * 是否启用字段检查
         * true: 自动检查表中是否存在数据范围字段，不存在则跳过过滤（推荐）
         * false: 不检查字段，强制应用数据范围（可能导致 SQL 错误）
         */
        private boolean fieldCheckEnabled = true;

        /**
         * 是否启用缓存
         * true: 缓存表字段信息和注解信息，提升性能（推荐）
         * false: 每次都重新检查，性能较低
         */
        private boolean cacheEnabled = true;

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public String getDeptField() {
            return deptField;
        }

        public void setDeptField(String deptField) {
            this.deptField = deptField;
        }

        public String getUserField() {
            return userField;
        }

        public void setUserField(String userField) {
            this.userField = userField;
        }

        public boolean isFieldCheckEnabled() {
            return fieldCheckEnabled;
        }

        public void setFieldCheckEnabled(boolean fieldCheckEnabled) {
            this.fieldCheckEnabled = fieldCheckEnabled;
        }

        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        /**
         * 数据范围工作模式
         */
        public enum Mode {
            /**
             * 注解模式：仅对标注 @DataScope 的方法生效
             * 优点：精确控制、性能好
             * 缺点：需要手动添加注解
             */
            ANNOTATION,

            /**
             * 全局模式：对所有查询生效
             * 优点：无需添加注解、安全性高
             * 缺点：性能开销较大
             */
            GLOBAL
        }
    }
}