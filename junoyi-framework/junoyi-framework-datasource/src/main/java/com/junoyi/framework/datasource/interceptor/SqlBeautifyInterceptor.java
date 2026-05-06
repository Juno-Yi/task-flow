package com.junoyi.framework.datasource.interceptor;

import com.junoyi.framework.datasource.properties.DataSourceProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * SQL 美化输出拦截器
 *
 * @author Fan
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlBeautifyInterceptor implements Interceptor {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SqlBeautifyInterceptor.class);
    private final DataSourceProperties properties;
    
    /**
     * 使用 ThreadLocal 保证 SimpleDateFormat 线程安全
     */
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_HOLDER = 
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public SqlBeautifyInterceptor(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!properties.isSqlLogEnabled() && !properties.isSqlBeautifyEnabled()) {
            return invocation.proceed();
        }

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();

        // 获取完整 SQL
        String completeSql = getCompleteSql(boundSql, statementHandler, parameterObject);

        if (properties.isSqlBeautifyEnabled()) {
            String beautifiedSql = beautifySql(completeSql);
            // 直接拼接字符串，避免占位符问题
            log.info("[SQL] " + beautifiedSql);
        } else {
            log.info("[SQL] " + completeSql);
        }

        return invocation.proceed();
    }

    /**
     * 获取完整 SQL（将 ? 替换为实际参数值）
     */
    private String getCompleteSql(BoundSql boundSql, StatementHandler statementHandler, Object parameterObject) {
        String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();
        
        if (parameterObject == null) {
            return sql;
        }

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return sql;
        }

        try {
            Configuration configuration = getConfiguration(statementHandler);
            if (configuration == null) {
                return sql;
            }

            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = configuration.newMetaObject(parameterObject);

            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value = null;

                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else if (metaObject.hasGetter(propertyName)) {
                    value = metaObject.getValue(propertyName);
                }

                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(formatValue(value)));
            }
        } catch (Exception e) {
            // 解析失败返回原始 SQL
        }

        return sql;
    }

    private Configuration getConfiguration(StatementHandler statementHandler) {
        try {
            Field field = statementHandler.getClass().getSuperclass().getDeclaredField("configuration");
            field.setAccessible(true);
            return (Configuration) field.get(statementHandler);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Date) {
            return "'" + DATE_FORMAT_HOLDER.get().format((Date) value) + "'";
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        }
        return value.toString();
    }

    /**
     * 美化 SQL
     */
    private String beautifySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n  ").append(sql
                .replaceAll("(?i)\\bFROM\\b", "\n  FROM")
                .replaceAll("(?i)\\bWHERE\\b", "\n  WHERE")
                .replaceAll("(?i)\\bAND\\b", "\n    AND")
                .replaceAll("(?i)\\bOR\\b", "\n    OR")
                .replaceAll("(?i)\\bORDER BY\\b", "\n  ORDER BY")
                .replaceAll("(?i)\\bGROUP BY\\b", "\n  GROUP BY")
                .replaceAll("(?i)\\bLIMIT\\b", "\n  LIMIT")
                .replaceAll("(?i)\\bLEFT JOIN\\b", "\n  LEFT JOIN")
                .replaceAll("(?i)\\bINNER JOIN\\b", "\n  INNER JOIN")
                .replaceAll("(?i)\\bVALUES\\b", "\n  VALUES")
                .replaceAll("(?i)\\bSET\\b", "\n  SET"));
        
        return sb.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
