package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringApplicationStartedEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

/**
 * SpringApplicationStartedEventAdapter是一个Spring事件适配器组件，用于将ApplicationStartedEvent转换为SpringApplicationStartedEvent。
 * 该类实现了SpringEventAdapter接口，专门处理应用启动完成事件的适配转换。
 *
 * @author Fan
 */
@Component
public class SpringApplicationStartedEventAdapter implements SpringEventAdapter<ApplicationStartedEvent> {

    /**
     * 判断当前适配器是否支持指定的Spring事件对象。
     * 当传入的事件对象是ApplicationStartedEvent类型时，返回true，表示支持该事件的处理。
     *
     * @param springEvent 待判断的Spring事件对象
     * @return 如果springEvent是ApplicationStartedEvent实例则返回true，否则返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ApplicationStartedEvent;
    }

    /**
     * 将Spring原生的ApplicationStartedEvent适配转换为SpringApplicationStartedEvent。
     * 通过提取原事件中的关键信息（Spring应用实例、启动参数、应用上下文、耗时等）构造新的事件对象。
     *
     * @param springEvent 待适配的Spring事件对象，必须是ApplicationStartedEvent类型
     * @return 转换后的SpringApplicationStartedEvent事件对象
     */
    @Override
    public Event adapt(Object springEvent) {
        ApplicationStartedEvent applicationStartedEvent = (ApplicationStartedEvent) springEvent;
        return new SpringApplicationStartedEvent(
                applicationStartedEvent.getSpringApplication(),
                applicationStartedEvent.getArgs(),
                applicationStartedEvent.getApplicationContext(),
                applicationStartedEvent.getTimeTaken()
        );
    }
}
