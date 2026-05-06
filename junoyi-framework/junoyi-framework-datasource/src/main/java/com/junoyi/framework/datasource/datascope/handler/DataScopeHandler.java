package com.junoyi.framework.datasource.datascope.handler;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder.DataScopeContext;
import com.junoyi.framework.datasource.datascope.DataScopeType;
import com.junoyi.framework.datasource.datascope.annotation.DataScope;
import com.junoyi.framework.datasource.datascope.annotation.IgnoreDataScope;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus 数据权限处理器
 * <p>
 * 实现 DataPermissionHandler 接口，自动为查询添加数据范围过滤条件。
 * 支持两种模式：
 * 1. 注解模式：只对标注了 @DataScope 的 Mapper 方法生效
 * 2. 全局模式：对所有查询生效（需要配置）
 * <p>
 * 优化逻辑：
 * - 移除白名单机制，改为动态检查表字段
 * - 只有当表中既没有 dept_id 也没有 user_id 字段时，才跳过数据范围过滤
 *
 * @author Fan
 */
public class DataScopeHandler implements DataPermissionHandler {

    /**
     * @IgnoreDataScope 注解缓存（避免重复反射）
     * key: mappedStatementId, value: 是否有忽略注解
     */
    private static final Map<String, Boolean> IGNORE_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * @DataScope 注解缓存（避免重复反射）
     * key: mappedStatementId, value: DataScope 注解（null 表示无注解）
     */
    private static final Map<String, DataScope> DATA_SCOPE_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * 表字段存在性缓存
     * key: tableName, value: FieldExistence
     */
    private static final Map<String, FieldExistence> TABLE_FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 是否启用全局数据范围（对所有查询生效）
     */
    private final boolean globalEnabled;

    /**
     * 默认部门字段名
     */
    private final String defaultDeptField;

    /**
     * 默认用户字段名
     */
    private final String defaultUserField;

    /**
     * 字段存在性内部类
     */
    private static class FieldExistence {
        boolean hasDeptField;
        boolean hasUserField;

        FieldExistence(boolean hasDeptField, boolean hasUserField) {
            this.hasDeptField = hasDeptField;
            this.hasUserField = hasUserField;
        }

        /**
         * 是否应该跳过数据范围过滤（两个字段都不存在）
         */
        boolean shouldSkip() {
            return !hasDeptField && !hasUserField;
        }
    }

    public DataScopeHandler() {
        this(false, "dept_id", "create_by");
    }

    public DataScopeHandler(boolean globalEnabled, String defaultDeptField, String defaultUserField) {
        this.globalEnabled = globalEnabled;
        this.defaultDeptField = defaultDeptField;
        this.defaultUserField = defaultUserField;
    }

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            // 检查是否有 @IgnoreDataScope 注解（使用缓存）
            if (hasIgnoreAnnotationCached(mappedStatementId)) {
                return where;
            }

            // 获取 @DataScope 注解配置（使用缓存）
            DataScope dataScope = getDataScopeAnnotationCached(mappedStatementId);

            // 非全局模式下，没有注解则不处理
            if (!globalEnabled && dataScope == null) {
                return where;
            }

            // 获取上下文（在确认需要处理数据范围后再获取）
            DataScopeContext context = DataScopeContextHolder.get();

            // 无上下文，直接放行（避免 NPE）
            if (context == null) {
                return where;
            }

            // 超级管理员，直接放行
            if (context.isSuperAdmin()) {
                return where;
            }

            // 全部数据权限，直接放行
            if (context.getScopeType() == null || context.getScopeType() == DataScopeType.ALL) {
                return where;
            }

            // 获取字段配置
            String tableAlias = dataScope != null ? dataScope.tableAlias() : "";
            String deptField = dataScope != null ? dataScope.deptField() : defaultDeptField;
            String userField = dataScope != null ? dataScope.userField() : defaultUserField;

            // 检查表字段是否存在（动态检查，移除白名单）
            String tableName = extractTableName(mappedStatementId);
            if (tableName != null) {
                FieldExistence fieldExistence = checkTableFields(tableName, deptField, userField);
                // 如果两个字段都不存在，跳过数据范围过滤
                if (fieldExistence != null && fieldExistence.shouldSkip()) {
                    return where;
                }

                // 根据字段存在性调整过滤逻辑
                Expression scopeExpression = buildScopeExpressionWithFieldCheck(context, tableAlias, deptField, userField, fieldExistence);
                if (scopeExpression == null) {
                    return where;
                }

                // 合并条件
                if (where == null) {
                    return scopeExpression;
                }
                return new AndExpression(where, new Parenthesis(scopeExpression));
            }

            // 无法获取表名时，使用原有逻辑
            Expression scopeExpression = buildScopeExpression(context, tableAlias, deptField, userField);
            if (scopeExpression == null) {
                return where;
            }

            // 合并条件
            if (where == null) {
                return scopeExpression;
            }
            return new AndExpression(where, new Parenthesis(scopeExpression));
        } catch (Exception e) {
            // 发生异常时记录日志并放行，避免影响业务
            System.err.println("[DataScopeHandler] 处理数据范围时发生异常: " + e.getMessage());
            e.printStackTrace();
            return where;
        }
    }

    /**
     * 从 mappedStatementId 提取表名
     */
    private String extractTableName(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            if (lastDot == -1) {
                return null;
            }

            String className = mappedStatementId.substring(0, lastDot);
            Class<?> mapperClass = Class.forName(className);

            // 尝试通过 MyBatis-Plus 的 TableInfoHelper 获取表信息
            // 通过 Mapper 接口的泛型参数获取实体类
            java.lang.reflect.Type[] types = mapperClass.getGenericInterfaces();
            if (types == null || types.length == 0) {
                return null;
            }

            for (java.lang.reflect.Type type : types) {
                if (type instanceof java.lang.reflect.ParameterizedType) {
                    java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) type;
                    java.lang.reflect.Type[] actualTypes = paramType.getActualTypeArguments();
                    if (actualTypes != null && actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                        Class<?> entityClass = (Class<?>) actualTypes[0];
                        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                        if (tableInfo != null) {
                            return tableInfo.getTableName();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // 忽略异常，返回 null
        }
        return null;
    }

    /**
     * 检查表中是否存在指定字段（使用缓存）
     */
    private FieldExistence checkTableFields(String tableName, String deptField, String userField) {
        if (tableName == null || tableName.isEmpty()) {
            // 无法获取表名时，默认认为字段存在（保守策略）
            return new FieldExistence(true, true);
        }

        String cacheKey = tableName + ":" + deptField + ":" + userField;
        return TABLE_FIELD_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                // 通过 MyBatis-Plus 的 TableInfoHelper 获取所有表信息
                List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();
                if (tableInfos != null) {
                    for (TableInfo tableInfo : tableInfos) {
                        if (tableInfo.getTableName() != null &&
                                tableInfo.getTableName().equalsIgnoreCase(tableName)) {
                            boolean hasDept = hasField(tableInfo, deptField);
                            boolean hasUser = hasField(tableInfo, userField);
                            return new FieldExistence(hasDept, hasUser);
                        }
                    }
                }
            } catch (Exception ignored) {
                // 忽略异常
            }
            // 默认认为字段存在（保守策略）
            return new FieldExistence(true, true);
        });
    }

    /**
     * 检查 TableInfo 中是否包含指定字段
     */
    private boolean hasField(TableInfo tableInfo, String fieldName) {
        if (tableInfo == null || fieldName == null || fieldName.isEmpty()) {
            return false;
        }

        // 检查主键字段
        if (tableInfo.getKeyColumn() != null && tableInfo.getKeyColumn().equalsIgnoreCase(fieldName)) {
            return true;
        }

        // 检查普通字段
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        if (fieldList != null) {
            for (TableFieldInfo fieldInfo : fieldList) {
                if (fieldInfo.getColumn() != null && fieldInfo.getColumn().equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 根据字段存在性构建数据范围过滤表达式
     */
    private Expression buildScopeExpressionWithFieldCheck(DataScopeContext context, String tableAlias,
                                                          String deptField, String userField,
                                                          FieldExistence fieldExistence) {
        if (context == null || fieldExistence == null) {
            return null;
        }

        String prefix = (tableAlias == null || tableAlias.isEmpty()) ? "" : tableAlias + ".";
        DataScopeType scopeType = context.getScopeType();

        if (scopeType == null) {
            return null;
        }

        switch (scopeType) {
            case DEPT:
            case DEPT_AND_CHILD:
                // 只有存在 dept_id 字段时才应用部门过滤
                if (fieldExistence.hasDeptField) {
                    Set<Long> deptIds = scopeType == DataScopeType.DEPT
                            ? context.getDeptIds()
                            : context.getAccessibleDeptIds();
                    return buildDeptInExpression(prefix + deptField, deptIds);
                }
                return null;

            case SELF:
                // 只有存在 user_id 字段时才应用用户过滤
                if (fieldExistence.hasUserField) {
                    return buildUserEqualsExpression(prefix + userField, context.getUserName());
                }
                return null;

            default:
                return null;
        }
    }

    /**
     * 检查是否有 @IgnoreDataScope 注解（使用缓存）
     */
    private boolean hasIgnoreAnnotationCached(String mappedStatementId) {
        return IGNORE_ANNOTATION_CACHE.computeIfAbsent(mappedStatementId, this::hasIgnoreAnnotation);
    }

    /**
     * 检查是否有 @IgnoreDataScope 注解
     */
    private boolean hasIgnoreAnnotation(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            String className = mappedStatementId.substring(0, lastDot);
            String methodName = mappedStatementId.substring(lastDot + 1);

            Class<?> mapperClass = Class.forName(className);

            // 检查类级别注解
            if (mapperClass.isAnnotationPresent(IgnoreDataScope.class)) {
                return true;
            }

            // 检查方法级别注解
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.isAnnotationPresent(IgnoreDataScope.class);
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 获取 Mapper 方法上的 @DataScope 注解（使用缓存）
     */
    private DataScope getDataScopeAnnotationCached(String mappedStatementId) {
        // ConcurrentHashMap 不允许 null 值，需要特殊处理
        // 使用 containsKey 检查是否已缓存
        if (DATA_SCOPE_ANNOTATION_CACHE.containsKey(mappedStatementId)) {
            return DATA_SCOPE_ANNOTATION_CACHE.get(mappedStatementId);
        }

        DataScope annotation = getDataScopeAnnotation(mappedStatementId);
        // 只有非 null 值才放入缓存
        // null 值表示没有注解，每次都重新检查（性能影响很小）
        if (annotation != null) {
            DATA_SCOPE_ANNOTATION_CACHE.put(mappedStatementId, annotation);
        }
        return annotation;
    }

    /**
     * 获取 Mapper 方法上的 @DataScope 注解
     */
    private DataScope getDataScopeAnnotation(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            String className = mappedStatementId.substring(0, lastDot);
            String methodName = mappedStatementId.substring(lastDot + 1);

            Class<?> mapperClass = Class.forName(className);
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 构建数据范围过滤表达式
     */
    private Expression buildScopeExpression(DataScopeContext context, String tableAlias,
                                            String deptField, String userField) {
        // 防御性检查
        if (context == null) {
            return null;
        }

        String prefix = (tableAlias == null || tableAlias.isEmpty()) ? "" : tableAlias + ".";

        DataScopeType scopeType = context.getScopeType();

        // scopeType 为 null 时不处理
        if (scopeType == null) {
            return null;
        }

        switch (scopeType) {
            case DEPT:
                return buildDeptInExpression(prefix + deptField, context.getDeptIds());

            case DEPT_AND_CHILD:
                return buildDeptInExpression(prefix + deptField, context.getAccessibleDeptIds());

            case SELF:
                return buildUserEqualsExpression(prefix + userField, context.getUserName());

            default:
                return null;
        }
    }

    /**
     * 构建部门 IN 表达式
     */
    private Expression buildDeptInExpression(String fieldName, Set<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            // 返回 1=0 表示无数据
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(fieldName));
        inExpression.setRightExpression(new ExpressionList(
                deptIds.stream().map(LongValue::new).collect(Collectors.toList())
        ));
        return inExpression;
    }

    /**
     * 构建用户等于表达式（使用用户名字符串）
     */
    private Expression buildUserEqualsExpression(String fieldName, String userName) {
        if (userName == null || userName.isEmpty()) {
            // 返回 1=0 表示无数据
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(fieldName));
        equalsTo.setRightExpression(new StringValue(userName));
        return equalsTo;
    }
}
