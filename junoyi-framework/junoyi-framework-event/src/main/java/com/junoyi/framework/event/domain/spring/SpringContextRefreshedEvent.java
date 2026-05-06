package com.junoyi.framework.event.domain.spring;

import org.springframework.context.ApplicationContext;

/**
 * Spring上下文刷新事件类
 * <p>
 * 该类继承自SpringApplicationContextEvent，用于表示Spring应用程序上下文刷新完成的事件。
 * 当Spring容器完成初始化或刷新操作后，会发布此事件通知监听器。
 * </p>
 *
 * @author Fan
 */
public class SpringContextRefreshedEvent extends SpringApplicationContextEvent{

    /**
     * 构造方法
     * <p>
     * 创建一个新的SpringContextRefreshedEvent实例
     * </p>
     *
     * @param source 触发事件的应用程序上下文对象，不能为null
     */
    public SpringContextRefreshedEvent(ApplicationContext source){
        super(source);
    }
}
