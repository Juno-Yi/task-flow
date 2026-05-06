package com.junoyi.framework.event.domain.spring;

import org.springframework.context.ApplicationContext;

/**
 * Spring上下文停止事件类
 * <p>
 * 该类继承自SpringContextClosedEvent，用于表示Spring应用上下文停止时触发的事件。
 * 当Spring容器被停止时，会发布此事件通知相关的监听器。
 * </p>
 *
 * @author Fan
 */
public class SpringContextStoppedEvent extends SpringContextClosedEvent{

    /**
     * 构造方法
     * <p>
     * 创建一个新的SpringContextStoppedEvent实例
     * </p>
     *
     * @param source 触发事件的应用上下文对象，不能为null
     */
    public SpringContextStoppedEvent(ApplicationContext source){
        super(source);
    }
}
