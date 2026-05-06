package com.junoyi.framework.datasource.datascope.annotation;

import java.lang.annotation.*;

/**
 * 数据范围注解
 * <p>
 * 标记在 Mapper 方法上，用于自动添加数据范围过滤条件
 * <p>
 * 使用示例：
 * <pre>
 * &#64;DataScope
 * List&lt;Order&gt; selectOrderList(OrderQuery query);
 *
 * &#64;DataScope(tableAlias = "o")
 * List&lt;Order&gt; selectOrderWithUser(OrderQuery query);
 *
 * &#64;DataScope(deptField = "department_id", userField = "creator_id")
 * List&lt;Order&gt; selectCustomFieldOrder(OrderQuery query);
 * </pre>
 *
 * @author Fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface DataScope {

    /**
     * 表别名（用于多表关联查询）
     */
    String tableAlias() default "";

    /**
     * 部门字段名，默认 dept_id
     */
    String deptField() default "dept_id";

    /**
     * 用户字段名（用于仅本人数据），默认 create_by
     */
    String userField() default "create_by";
}
