package com.junoyi.framework.event.annotation;

import com.junoyi.framework.event.enums.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 事件监听器
 *
 * @author Fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * 事件监听处理优先级（默认优先级0）
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * 是否异步执行（默认同步）
     */
    boolean async() default false;
}
