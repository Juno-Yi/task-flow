package com.junoyi.framework.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * 事件监听器类注解
 * 本注解用于来标记用于事件监听器的类
 * 如果需要注册自己的事件监听器类，则需要在类上添加这个注解，用于进行注册
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface EventListener {

    @AliasFor(annotation = Component.class)
    String value() default "";
}
