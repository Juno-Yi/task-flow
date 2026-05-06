package com.junoyi.framework.event.core;

import com.junoyi.framework.event.enums.EventPriority;

import java.lang.reflect.Method;

/**
 * 注册的事件处理器记录类
 *
 * 该记录类用于封装一个已注册的事件监听器的信息，
 * 包括监听器实例、处理方法、事件优先级和是否异步执行。
 *
 * @param listener 监听器实例，用于接收和处理事件
 * @param method 处理事件的方法，该方法将被调用以响应事件
 * @param priority 事件处理优先级，决定监听器在事件处理链中的执行顺序
 * @param async 是否异步执行，true表示异步执行，false表示同步执行
 *
 * @author Fan
 */
public record RegisteredHandler (
        Object listener,
        Method method,
        EventPriority priority,
        boolean async
) {}
