package com.junoyi.framework.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据源配置属性
 *
 * @author Fan
 */
@ConfigurationProperties(prefix = "junoyi.datasource")
public class DataSourceProperties {

    /**
     * 是否启用 SQL 美化输出
     */
    private boolean sqlBeautifyEnabled = true;

    /**
     * 是否启用慢 SQL 监控
     */
    private boolean slowSqlEnabled = true;

    /**
     * 慢 SQL 阈值（毫秒）
     */
    private long slowSqlThreshold = 3000;

    /**
     * 是否启用 SQL 日志输出
     */
    private boolean sqlLogEnabled = true;

    /**
     * 是否启用乐观锁插件
     */
    private boolean optimisticLockerEnable = true;

    /**
     * 是否启用防止全表更新删除插件
     */
    private boolean blockAttackEnable = true;


    public boolean isSqlBeautifyEnabled() {
        return sqlBeautifyEnabled;
    }

    public void setSqlBeautifyEnabled(boolean sqlBeautifyEnabled) {
        this.sqlBeautifyEnabled = sqlBeautifyEnabled;
    }

    public boolean isSlowSqlEnabled() {
        return slowSqlEnabled;
    }

    public void setSlowSqlEnabled(boolean slowSqlEnabled) {
        this.slowSqlEnabled = slowSqlEnabled;
    }

    public long getSlowSqlThreshold() {
        return slowSqlThreshold;
    }

    public void setSlowSqlThreshold(long slowSqlThreshold){
        this.slowSqlThreshold = slowSqlThreshold;
    }

    public boolean isSqlLogEnabled() {
        return sqlLogEnabled;
    }

    public void setSqlLogEnabled(boolean sqlLogEnabled) {
        this.sqlLogEnabled = sqlLogEnabled;
    }

    public void setOptimisticLockerEnable(boolean optimisticLockerEnable){
        this.optimisticLockerEnable = optimisticLockerEnable;
    }

    public boolean isOptimisticLockerEnable() {
        return optimisticLockerEnable;
    }

    public void setBlockAttackEnable(boolean blockAttackEnable){
        this.blockAttackEnable = blockAttackEnable;
    }

    public boolean isBlockAttackEnable(){
        return blockAttackEnable;
    }
}
