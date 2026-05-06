package com.junoyi.framework.permission.annotation;

import com.junoyi.framework.permission.enums.Logical;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * <p>
 * 用于标记需要权限校验的方法或类，支持权限节点通配符匹配
 * <p>
 * 使用示例：
 * <pre>
 * // 单个权限
 * &#64;Permission("system.user.delete")
 *
 * // 多个权限（OR 关系，默认）
 * &#64;Permission({"system.user.delete", "system.admin"})
 *
 * // 多个权限（AND 关系）
 * &#64;Permission(value = {"system.user.view", "system.user.edit"}, logical = Logical.AND)
 * </pre>
 *
 * @author Fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Permission {

    /**
     * 权限节点（支持通配符）
     * <p>
     * 示例：
     * <ul>
     *   <li>system.user.delete - 精确匹配</li>
     *   <li>system.user.* - 匹配 system.user 下的所有操作</li>
     *   <li>system.** - 匹配 system 下的所有层级</li>
     * </ul>
     */
    String[] value() default {};

    /**
     * 多个权限之间的逻辑关系
     * <p>
     * AND: 需要同时拥有所有权限
     * OR: 拥有任意一个权限即可（默认）
     */
    Logical logical() default Logical.OR;

    /**
     * 是否要求登录（默认 true）
     * <p>
     * true: 未登录直接拒绝
     * false: 允许匿名访问（但仍需满足权限要求）
     */
    boolean requireLogin() default true;
}
