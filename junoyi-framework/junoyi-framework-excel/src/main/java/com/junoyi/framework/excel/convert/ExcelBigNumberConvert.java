package com.junoyi.framework.excel.convert;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;

/**
 * 大数值转换
 * 用于处理Excel中大数值的转换，避免JavaScript Number类型精度丢失问题
 *
 * @author Fan
 */
public class ExcelBigNumberConvert implements Converter<Long> {

    /**
     * 获取支持的Java数据类型
     *
     * @return 支持转换的Java类型，此处为Long类型
     */
    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    /**
     * 获取支持的Excel单元格数据类型
     *
     * @return 支持的Excel数据类型，此处为字符串类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 将Excel数据转换为Java数据
     *
     * @param cellData Excel单元格数据
     * @param contentProperty Excel内容属性
     * @param globalConfiguration 全局配置信息
     * @return 转换后的Long类型数据
     */
    @Override
    public Long convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return Convert.toLong(cellData.getData());
    }

    /**
     * 将Java数据转换为Excel数据
     * 对于超过15位的长整型数字，转换为字符串格式以避免精度丢失
     * 对于15位及以下的数字，转换为BigDecimal并以数字格式写入Excel
     *
     * @param object Java对象数据（Long类型）
     * @param contentProperty Excel内容属性
     * @param globalConfiguration 全局配置信息
     * @return 转换后的Excel单元格数据
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Long object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 检查对象是否为空，如果不为空则检查字符串长度是否超过15位
        if (ObjectUtil.isNotNull(object)) {
            String str = Convert.toStr(object);
            if (str.length() > 15) {
                return new WriteCellData<>(str);
            }
        }
        // 对于15位及以下的数字，使用BigDecimal以数字格式写入
        WriteCellData<Object> cellData = new WriteCellData<>(new BigDecimal(object));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }

}
