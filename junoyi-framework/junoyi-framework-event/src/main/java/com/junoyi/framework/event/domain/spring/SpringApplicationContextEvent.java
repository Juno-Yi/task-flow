package com.junoyi.framework.event.domain.spring;

import com.junoyi.framework.event.domain.BaseEvent;
import org.springframework.context.ApplicationContext;

/**
 * Spring应用上下文事件类
 * <p>
 * 该类继承自BaseEvent，用于封装Spring ApplicationContext相关的事件信息
 * </p>
 *
 * @author Fan
 */
public class SpringApplicationContextEvent extends BaseEvent {
    private final Object source;

    /**
     * 构造方法，创建一个新的SpringApplicationContextEvent实例
     *
     * @param source 应用上下文对象，作为事件源
     */
    public SpringApplicationContextEvent(ApplicationContext source){
        this.source = source;
    }

    /**
     * 获取应用上下文对象
     *
     * @return ApplicationContext 应用上下文实例
     */
    public ApplicationContext getApplicationContext(){
        return (ApplicationContext) source;
    }

}
