package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringContextStoppedEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * SpringContextStoppedEventAdapter类用于适配Spring上下文停止事件
 * 该类实现了SpringEventAdapter接口，专门处理ContextStoppedEvent类型的事件
 *
 * @author Fan
 */
@Component
public class SpringContextStoppedEventAdapter implements SpringEventAdapter<ContextStoppedEvent> {

    /**
     * 判断是否支持指定的Spring事件
     * @param springEvent 待判断的Spring事件对象
     * @return boolean 返回false，表示当前不支持任何事件
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ContextStoppedEvent;
    }

    /**
     * 将Spring事件适配为通用事件
     * @param springEvent 需要适配的Spring事件对象
     * @return Event 适配后的事件对象，当前实现返回null
     */
    @Override
    public Event adapt(Object springEvent) {
        ContextStoppedEvent contextStoppedEvent = (ContextStoppedEvent) springEvent;
        return new SpringContextStoppedEvent(contextStoppedEvent.getApplicationContext());
    }
}
