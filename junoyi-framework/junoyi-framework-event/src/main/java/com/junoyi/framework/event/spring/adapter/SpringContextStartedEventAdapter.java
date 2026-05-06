package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringContextStartedEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

/**
 * Spring上下文启动事件适配器类
 * 用于将Spring的ContextStartedEvent事件转换为通用的Event对象
 *
 * @author Fan
 */
@Component
public class SpringContextStartedEventAdapter implements SpringEventAdapter<ContextStartedEvent> {

    /**
     * 判断当前适配器是否支持指定的Spring事件
     *
     * @param springEvent 待判断的Spring事件对象
     * @return 是否支持该事件，当前实现始终返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ContextStartedEvent;
    }

    /**
     * 将Spring事件适配转换为通用事件对象
     *
     * @param springEvent 需要转换的Spring事件对象
     * @return 转换后的通用事件对象，当前实现始终返回null
     */
    @Override
    public Event adapt(Object springEvent) {
        ContextStartedEvent contextStartedEvent = (ContextStartedEvent) springEvent;
        return new SpringContextStartedEvent(contextStartedEvent.getApplicationContext());
    }
}
