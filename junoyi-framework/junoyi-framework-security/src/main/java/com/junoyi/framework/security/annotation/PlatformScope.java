package com.junoyi.framework.security.annotation;

import com.junoyi.framework.security.enums.PlatformType;

import java.lang.annotation.*;

/**
 * PlatformScope注解用于标记方法的平台作用域范围
 * 该注解可以在运行时被反射获取，并且会包含在JavaDoc文档中
 *
 * @author Fan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlatformScope {

    /**
     * 获取平台类型数组值
     *
     * @return PlatformType数组，默认为空数组
     */
    PlatformType[] value() default {};
}
