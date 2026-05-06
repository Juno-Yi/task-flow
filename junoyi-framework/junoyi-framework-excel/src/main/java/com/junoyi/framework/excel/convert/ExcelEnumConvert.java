package com.junoyi.framework.excel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.junoyi.framework.core.utils.reflect.ReflectUtils;
import com.junoyi.framework.excel.annotation.ExcelEnumFormat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Excel枚举转换器，用于在Excel读写过程中将枚举类型的code与text进行相互转换。
 *
 * @author Fan
 */
public class ExcelEnumConvert implements Converter<Object> {

    /**
     * 获取支持的Java类型。
     *
     * @return 支持转换的Java类型，此处为Object.class表示支持所有对象类型。
     */
    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    /**
     * 获取支持的Excel单元格数据类型。
     *
     * @return 支持的Excel数据类型，返回null表示不限制具体类型。
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    /**
     * 将Excel中的单元格数据转换为Java对象属性值。
     *
     * @param cellData             Excel单元格数据
     * @param contentProperty      Excel内容属性信息
     * @param globalConfiguration  全局配置信息
     * @return 转换后的Java对象属性值
     */
    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Object codeValue = cellData.getData();
        // 如果是空值
        if (ObjectUtil.isNull(codeValue)) {
            return null;
        }
        Map<Object, String> enumValueMap = beforeConvert(contentProperty);
        String textValue = enumValueMap.get(codeValue);
        return Convert.convert(contentProperty.getField().getType(), textValue);
    }

    /**
     * 将Java对象属性值转换为Excel单元格数据。
     *
     * @param object               Java对象属性值
     * @param contentProperty      Excel内容属性信息
     * @param globalConfiguration  全局配置信息
     * @return 转换后的Excel单元格数据
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        }
        Map<Object, String> enumValueMap = beforeConvert(contentProperty);
        String value = Convert.toStr(enumValueMap.get(object), "");
        return new WriteCellData<>(value);
    }

    /**
     * 在转换前准备枚举值映射关系。
     *
     * @param contentProperty Excel内容属性信息
     * @return 枚举code到text的映射表
     */
    private Map<Object, String> beforeConvert(ExcelContentProperty contentProperty) {
        ExcelEnumFormat anno = getAnnotation(contentProperty.getField());
        Map<Object, String> enumValueMap = new HashMap<>();
        Enum<?>[] enumConstants = anno.enumClass().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            Object codeValue = ReflectUtils.invokeGetter(enumConstant, anno.codeField());
            String textValue = ReflectUtils.invokeGetter(enumConstant, anno.textField());
            enumValueMap.put(codeValue, textValue);
        }
        return enumValueMap;
    }

    /**
     * 获取字段上的ExcelEnumFormat注解。
     *
     * @param field 字段对象
     * @return ExcelEnumFormat注解实例
     */
    private ExcelEnumFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelEnumFormat.class);
    }
}
