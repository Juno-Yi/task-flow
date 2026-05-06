package com.junoyi.framework.event.domain.spring;

import com.junoyi.framework.event.domain.BaseEvent;
import org.springframework.boot.SpringApplication;

/**
 * SpringApplicationEvent类用于表示Spring应用程序事件
 * 继承自BaseEvent，包含事件源和命令行参数信息
 *
 * @author Fan
 */
public class SpringApplicationEvent extends BaseEvent {

    private final Object source;

    private final String[] args;

    /**
     * 构造函数，创建一个新的SpringApplicationEvent实例
     * @param application Spring应用程序实例，作为事件源
     * @param args 命令行参数数组
     */
    public SpringApplicationEvent(SpringApplication application, String[] args){
        this.source = application;
        this.args = args;
    }

    /**
     * 获取Spring应用程序实例
     * @return 返回事件源对象，并将其转换为SpringApplication类型
     */
    public SpringApplication getSpringApplication(){
        return (SpringApplication) source;
    }

    /**
     * 获取命令行参数数组
     * @return 返回存储的命令行参数数组
     */
    public final String[] getArgs(){
        return args;
    }
}
