package com.junoyi.framework.excel.core;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * Excel监听器接口，用于处理Excel读取过程中的事件监听
 *
 * @param <T> 泛型类型，表示Excel数据对应的实体类类型
 *
 * @author Fan
 */
public interface ExcelListener<T> extends ReadListener<T> {

    /**
     * 获取Excel处理结果
     *
     * @return ExcelResult<T> Excel处理结果对象，包含解析后的数据列表和其他相关信息
     */
    ExcelResult<T> getExcelResult();
}
