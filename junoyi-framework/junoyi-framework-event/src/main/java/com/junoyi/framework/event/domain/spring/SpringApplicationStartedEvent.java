package com.junoyi.framework.event.domain.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

/**
 * SpringApplicationStartedEvent类表示Spring应用程序启动事件。
 * 该事件在Spring应用程序上下文刷新之后、任何应用程序运行监听器启动之前触发。
 *
 * @author Fan
 */
public class SpringApplicationStartedEvent extends SpringApplicationEvent{

    /**
     * 可配置的应用程序上下文实例
     */
    private final ConfigurableApplicationContext context;

    /**
     * 应用程序启动所花费的时间
     */
    private final Duration timeTaken;

    /**
     * 构造一个新的SpringApplicationStartedEvent实例。
     *
     * @param application Spring应用程序实例
     * @param args 启动参数数组
     * @param context 可配置的应用程序上下文
     * @param timeTaken 启动过程耗时
     */
    public SpringApplicationStartedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Duration timeTaken){
        super(application,args);
        this.context = context;
        this.timeTaken = timeTaken;
    }

    /**
     * 获取关联的Spring应用程序实例。
     *
     * @return SpringApplication实例
     */
    @Override
    public SpringApplication getSpringApplication() {
        return super.getSpringApplication();
    }

    /**
     * 获取应用程序启动所花费的时间。
     *
     * @return Duration类型的耗时信息
     */
    public Duration getTimeTaken() {
        return timeTaken;
    }
}
