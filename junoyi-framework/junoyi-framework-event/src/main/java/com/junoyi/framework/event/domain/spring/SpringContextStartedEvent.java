package com.junoyi.framework.event.domain.spring;

import org.springframework.context.ApplicationContext;

/**
 * SpringContextStartedEvent类表示Spring应用上下文启动事件。
 * 该事件在Spring应用上下文完成启动后触发，用于通知监听器应用上下文已成功启动。
 *
 * @author Fan
 */
public class SpringContextStartedEvent extends SpringApplicationContextEvent {

    /**
     * 构造一个SpringContextStartedEvent实例。
     *
     * @param source 触发此事件的应用上下文对象，不能为null
     */
    public SpringContextStartedEvent(ApplicationContext source){
        super(source);
    }
}
