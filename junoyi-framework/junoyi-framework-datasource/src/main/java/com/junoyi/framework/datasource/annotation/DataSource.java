package com.junoyi.framework.datasource.annotation;

import com.junoyi.framework.datasource.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * 数据源切换注解
 * 用于方法或类上，指定使用哪个数据源
 *
 * @author Fan
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {

    /**
     * 数据源类型
     * @return 数据源类型枚举值，默认为主数据源
     */
    DataSourceType value() default DataSourceType.MASTER;
}

