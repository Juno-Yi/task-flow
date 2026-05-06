package com.junoyi.framework.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.junoyi.framework.datasource.datascope.handler.DataScopeHandler;
import com.junoyi.framework.datasource.interceptor.SqlBeautifyInterceptor;
import com.junoyi.framework.datasource.interceptor.SlowSqlInterceptor;
import com.junoyi.framework.datasource.properties.DataPermissionProperties;
import com.junoyi.framework.datasource.properties.DataSourceProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置类
 *
 * 功能：
 * - 分页插件
 * - 乐观锁插件
 * - 防止全表更新删除插件
 * - SQL 美化输出
 * - 慢 SQL 监控
 * - 数据范围权限控制
 *
 * @author Fan
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties({DataSourceProperties.class, DataPermissionProperties.class})
@MapperScan("com.junoyi.**.mapper")
public class MyBatisPlusConfig {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(MyBatisPlusConfig.class);

    /**
     * 创建并配置 MyBatis-Plus 拦截器实例。
     * <p>
     * 包含以下功能插件：
     * <ul>
     *   <li>数据权限插件：自动添加数据范围过滤条件</li>
     *   <li>分页插件：支持数据库分页查询</li>
     *   <li>乐观锁插件：用于处理并发更新场景下的版本控制</li>
     *   <li>防全表更新/删除插件：防止误操作导致的数据批量变更</li>
     * </ul>
     *
     * @param dataPermissionProperties 数据权限配置属性
     * @return 初始化完成的 MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataPermissionProperties dataPermissionProperties, DataSourceProperties dataSourceProperties) {
        log.info("Start initializing MyBatis-Plus interceptor.");

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加数据权限插件（必须在分页插件之前）
        if (dataPermissionProperties.isEnabled()) {
            DataPermissionProperties.DataScopeConfig dataScopeConfig = dataPermissionProperties.getDataScope();
            boolean isGlobalMode = dataScopeConfig.getMode() == DataPermissionProperties.DataScopeConfig.Mode.GLOBAL;

            log.info("Initializing DataPermission plugin (mode: {}, fieldCheck: {}, cache: {}).",
                    dataScopeConfig.getMode(),
                    dataScopeConfig.isFieldCheckEnabled(),
                    dataScopeConfig.isCacheEnabled());

            DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor(
                    new DataScopeHandler(
                            isGlobalMode,
                            dataScopeConfig.getDeptField(),
                            dataScopeConfig.getUserField()
                    )
            );
            interceptor.addInnerInterceptor(dataPermissionInterceptor);
        } else {
            log.info("DataPermission plugin is disabled.");
        }

        // 添加分页插件，并设置相关参数
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L); // 单页最大数量限制
        paginationInnerInterceptor.setOverflow(false); // 溢出总页数后是否进行处理
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 添加乐观锁插件
        if (dataSourceProperties.isOptimisticLockerEnable())
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 添加防止全表更新删除插件
        if (dataSourceProperties.isBlockAttackEnable())
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        log.info("Initialization MyBatis-Plus interceptor completed.");
        return interceptor;
    }

    /**
     * 创建 SQL 美化输出拦截器 Bean。
     * <p>
     * 该拦截器用于美化打印的 SQL 语句，便于开发调试时查看执行的 SQL 内容。
     * 仅在启用时才创建 Bean，避免不必要的拦截开销。
     *
     * @param properties 数据源配置属性
     * @return SqlBeautifyInterceptor 实例
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "junoyi.datasource",
            name = "sql-beautify-enabled",
            havingValue = "true"
    )
    public SqlBeautifyInterceptor sqlBeautifyInterceptor(DataSourceProperties properties) {
        log.info("SQL beautify interceptor enabled.");
        return new SqlBeautifyInterceptor(properties);
    }

    /**
     * 创建慢 SQL 监控拦截器 Bean。
     * <p>
     * 用于监控执行时间较长的 SQL 语句，帮助识别性能瓶颈。
     * 仅在启用时才创建 Bean，避免不必要的拦截开销。
     *
     * @param properties 数据源配置属性
     * @return SlowSqlInterceptor 实例
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "junoyi.datasource",
            name = "slow-sql-enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SlowSqlInterceptor slowSqlInterceptor(DataSourceProperties properties) {
        log.info("Slow SQL interceptor enabled, threshold: {}ms.", properties.getSlowSqlThreshold());
        return new SlowSqlInterceptor(properties);
    }
}
