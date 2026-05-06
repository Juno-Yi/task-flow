package com.junoyi.framework.datasource.interceptor;

import com.junoyi.framework.datasource.properties.DataSourceProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.Properties;

/**
 * 慢 SQL 监控拦截器
 * 记录执行时间超过阈值的 SQL 语句
 *
 * @author Fan
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SlowSqlInterceptor implements Interceptor {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SlowSqlInterceptor.class);
    private final DataSourceProperties properties;

    public SlowSqlInterceptor(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 检查是否启用慢 SQL 监控
        if (!properties.isSlowSqlEnabled()) {
            return invocation.proceed();
        }

        long startTime = System.currentTimeMillis();

        try {
            return invocation.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // 如果执行时间超过阈值，记录慢 SQL
            if (executionTime >= properties.getSlowSqlThreshold()) {
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
                BoundSql boundSql = statementHandler.getBoundSql();
                String sql = boundSql.getSql();

                log.warn("[慢SQL] 执行耗时: {}ms\nSQL: {}\n参数: {}",
                        executionTime, 
                        sql.replaceAll("\\s+", " ").trim(), 
                        boundSql.getParameterObject());
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
