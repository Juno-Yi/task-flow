package com.junoyi.framework.excel.core;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的Excel解析结果实现类
 * 用于存储Excel解析后的数据列表和错误信息列表
 *
 * @param <T> 数据类型泛型
 *
 * @author Fan
 */
public class DefaultExcelResult<T> implements ExcelResult<T> {

    /**
     * 解析成功的数据列表
     */
    private List<T> list;

    /**
     * 解析失败的错误信息列表
     */
    private List<String> errorList;

    /**
     * 无参构造函数
     * 初始化空的数据列表和错误列表
     */
    public DefaultExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
    }

    /**
     * 带参数的构造函数
     *
     * @param list      解析成功的数据列表
     * @param errorList 解析失败的错误信息列表
     */
    public DefaultExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    /**
     * 拷贝构造函数
     *
     * @param excelResult Excel解析结果对象
     */
    public DefaultExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList();
        this.errorList = excelResult.getErrorList();
    }

    /**
     * 获取解析成功的数据列表
     *
     * @return 解析成功的数据列表
     */
    @Override
    public List<T> getList() {
        return list;
    }

    /**
     * 获取解析失败的错误信息列表
     *
     * @return 解析失败的错误信息列表
     */
    @Override
    public List<String> getErrorList() {
        return errorList;
    }

    /**
     * 获取解析结果分析信息
     * 根据成功和失败的数据数量返回相应的提示信息
     *
     * @return 解析结果分析字符串
     */
    @Override
    public String getAnalysis() {
        int successCount = list.size();
        int errorCount = errorList.size();
        // 如果没有解析到任何数据，返回读取失败提示
        if (successCount == 0) {
            return "读取失败，未解析到数据";
        } else {
            // 如果有解析成功的数据
            if (errorCount == 0) {
                // 没有错误信息，返回全部成功提示
                return StrUtil.format("恭喜您，全部读取成功！共{}条", successCount);
            } else {
                return "";
            }
        }
    }
}
