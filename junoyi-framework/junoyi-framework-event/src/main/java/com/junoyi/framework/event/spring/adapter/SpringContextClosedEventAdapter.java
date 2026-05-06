package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringContextClosedEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * Spring上下文关闭事件适配器类
 * 用于将Spring的ContextClosedEvent事件转换为系统内部的SpringContextClosedEvent事件
 *
 * @author Fan
 */
@Component
public class SpringContextClosedEventAdapter implements SpringEventAdapter<ContextClosedEvent> {

    /**
     * 判断是否支持处理指定的Spring事件
     * @param springEvent 待判断的Spring事件对象
     * @return 如果事件是ContextClosedEvent类型则返回true，否则返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ContextClosedEvent;
    }

    /**
     * 将Spring事件适配转换为系统内部事件
     * @param springEvent 待转换的Spring事件对象，必须是ContextClosedEvent类型
     * @return 转换后的SpringContextClosedEvent事件对象
     */
    @Override
    public Event adapt(Object springEvent) {
        ContextClosedEvent contextClosedEvent = (ContextClosedEvent) springEvent;
        return new SpringContextClosedEvent(contextClosedEvent.getApplicationContext());
    }
}
