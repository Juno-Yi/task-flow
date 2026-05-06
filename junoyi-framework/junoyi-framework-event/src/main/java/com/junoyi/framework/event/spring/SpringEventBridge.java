package com.junoyi.framework.event.spring;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring与JunoYi事件桥
 * 通过Event Bridge事件桥，将框架的事件机制和Spring的事件打通，
 * 调用Spring Event将不会再繁琐，只需要实现JunoYi 中的事件监听器，即可监听处理Spring事件
 *
 * @author Fan
 */
@Component
public class SpringEventBridge implements ApplicationListener<ApplicationEvent> {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SpringEventBridge.class);

    private final List<SpringEventAdapter<?>> adapters;

    private final EventBus eventBus;

    /**
     * 构造函数
     *
     * @param adapters Spring事件适配器列表，用于将Spring事件转换为JunoYi事件
     * @param eventBus JunoYi事件总线，用于发布转换后的事件
     */
    public SpringEventBridge(
            List<SpringEventAdapter<?>> adapters,
            EventBus eventBus
    ){
        this.adapters = adapters;
        this.eventBus = eventBus;
        log.info("SpringEventBridge initialized with {} adapters", adapters.size());

    }

    /**
     * 处理Spring应用事件
     * 遍历所有适配器，找到支持当前事件的适配器，将其转换为JunoYi事件并发布
     *
     * @param event Spring应用事件
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 遍历适配器列表，寻找支持当前事件的适配器进行事件转换和发布
        for (SpringEventAdapter<?> adapter : adapters){
            if (adapter.supports(event)){
                Event adaptedEvent = adapter.adapt(event);
                eventBus.callEvent(adaptedEvent);
            }
        }
    }
}
