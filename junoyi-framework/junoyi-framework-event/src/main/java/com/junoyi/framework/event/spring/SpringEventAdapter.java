package com.junoyi.framework.event.spring;

import com.junoyi.framework.event.core.Event;

/**
 * Spring事件适配器接口，用于将Spring事件转换为通用事件格式
 * @param <E> 事件类型参数
 */
public interface SpringEventAdapter<E> {

    /**
     * 判断当前适配器是否支持处理指定的Spring事件
     * @param springEvent Spring事件对象
     * @return 如果支持处理该事件则返回true，否则返回false
     */
    boolean supports(Object springEvent);

    /**
     * 将Spring事件适配转换为通用事件对象
     * @param springEvent Spring事件对象
     * @return 转换后的通用事件对象
     */
    Event adapt(Object springEvent);
}
