package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringContextRefreshedEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Spring上下文刷新事件适配器类
 * 用于将Spring的ContextRefreshedEvent事件转换为系统内部的Event事件
 *
 * @author Fan
 */
@Component
public class SpringContextRefreshedEventAdapter implements SpringEventAdapter<ContextRefreshedEvent> {

    /**
     * 判断是否支持指定的Spring事件
     * @param springEvent 待判断的Spring事件对象
     * @return 是否支持该事件，当前实现始终返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ContextRefreshedEvent;
    }

    /**
     * 将Spring事件适配转换为系统内部事件
     * @param springEvent 待转换的Spring事件对象
     * @return 转换后的系统内部事件，当前实现始终返回null
     */
    @Override
    public Event adapt(Object springEvent) {
        ContextRefreshedEvent contextRefreshedEvent = (ContextRefreshedEvent) springEvent;
        return new SpringContextRefreshedEvent(contextRefreshedEvent.getApplicationContext());
    }
}
