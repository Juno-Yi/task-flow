package com.junoyi.framework.excel.core;

import java.util.List;

/**
 * Excel处理结果接口
 * 定义了Excel数据处理后的结果规范，包含处理后的数据列表、错误信息列表和分析结果
 * @param <T> 泛型类型，表示处理后的数据对象类型
 *
 * @author Fan
 */
public interface ExcelResult<T> {
    /**
     * 获取处理后的数据列表
     * @return 包含处理后数据对象的列表
     */
    List<T> getList();

    /**
     * 获取处理过程中产生的错误信息列表
     * @return 包含错误信息的字符串列表
     */
    List<String> getErrorList();

    /**
     * 获取数据分析结果
     * @return 分析结果的字符串描述
     */
    String getAnalysis();
}
