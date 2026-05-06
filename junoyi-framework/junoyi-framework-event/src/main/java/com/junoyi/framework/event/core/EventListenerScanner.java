package com.junoyi.framework.event.core;

import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听器扫描器
 * 扫描获取所有的事件监听器，在框架启动时期扫描
 *
 * @author Fan
 */
public class EventListenerScanner implements BeanPostProcessor, Ordered, ApplicationListener<ContextRefreshedEvent> {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(EventListenerScanner.class);

    private final EventBus eventBus;

    private final Set<String> registeredBeanNames = ConcurrentHashMap.newKeySet();

    private final List<String> registeredListenerClasses = Collections.synchronizedList(new ArrayList<>());

    /**
     * 构造函数
     *
     * @param eventBus 事件总线实例，用于注册事件监听器
     */
    public EventListenerScanner(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Bean初始化后处理方法
     * 扫描带有@EventListener注解的Bean并注册为事件监听器
     *
     * @param bean     Spring容器中的Bean实例
     * @param beanName Bean的名称
     * @return 处理后的Bean实例
     * @throws BeansException Bean处理异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取Bean的真实类型
        Class<?> userClass = ClassUtils.getUserClass(bean);
        // 检查Bean是否带有@EventListener注解
        if (!userClass.isAnnotationPresent(EventListener.class)) {
            return bean;
        }

        // 避免重复注册同一个Bean
        if (registeredBeanNames.add(beanName)) {
            eventBus.registerListener(bean);
            registeredListenerClasses.add(userClass.getName());
        }
        return bean;
    }

    /**
     * 应用上下文刷新事件回调方法
     * 当Spring应用上下文刷新完成时触发此方法，用于输出已注册的事件监听器信息
     *
     * @param event 上下文刷新事件对象，包含应用上下文刷新的相关信息
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (registeredListenerClasses.isEmpty()) {
            log.info("EventListenerScanner", "No @EventListener beans found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Registered event listeners (count=")
                .append(registeredListenerClasses.size())
                .append("):\n");

        for (String className : registeredListenerClasses) {
            sb.append("  - ").append(className).append("\n");
        }
        log.info("EventListenerScanner", sb.toString().trim());
    }

    /**
     * 获取处理器的执行顺序
     * 返回最高优先级，确保事件监听器尽早注册
     *
     * @return 处理器执行顺序值，值越小优先级越高
     */
    @Override
    public int getOrder() {
        // 尽量早注册（越小越早）
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
