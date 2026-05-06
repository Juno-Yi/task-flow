package com.junoyi.framework.excel.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.junoyi.framework.core.utils.StreamUtils;
import com.junoyi.framework.core.utils.ValidatorUtils;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 默认的Excel监听器实现类，用于处理Excel文件的数据读取、校验及异常处理。
 * 继承自 {@link AnalysisEventListener} 并实现了 {@link ExcelListener} 接口。
 *
 * @param <T> Excel数据对应的实体类型
 *
 * @author Fan
 */
@NoArgsConstructor
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(DefaultExcelListener.class);
    private Boolean isValidate = Boolean.TRUE;
    private Map<Integer, String> headMap;
    private ExcelResult<T> excelResult;

    /**
     * 构造方法，初始化是否需要进行数据校验。
     *
     * @param isValidate 是否启用数据校验功能，默认为 true
     */
    public DefaultExcelListener(boolean isValidate) {
        this.excelResult = new DefaultExcelResult<>();
        this.isValidate = isValidate;
    }

    /**
     * 处理解析过程中发生的异常信息，并记录错误日志。
     * 支持两种类型的异常：
     * - {@link ExcelDataConvertException}: 单元格转换异常，可定位到具体的行列位置。
     * - {@link ConstraintViolationException}: 数据校验失败异常，通常由 Bean Validation 触发。
     *
     * @param exception 解析过程中的异常对象
     * @param context   当前分析上下文环境
     * @throws Exception 抛出封装后的 {@link ExcelAnalysisException} 异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        String errMsg = null;
        if (exception instanceof ExcelDataConvertException excelDataConvertException) {
            // 获取发生异常的具体行号与列号并格式化提示信息
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = StrUtil.format("第{}行-第{}列-表头{}: 解析异常<br/>",
                    rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
            if (log.isDebugEnabled()) {
                log.error(errMsg);
            }
        }
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            // 提取所有违反约束的信息并拼接成字符串
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            String constraintViolationsMsg = StreamUtils.join(constraintViolations, ConstraintViolation::getMessage, ", ");
            errMsg = StrUtil.format("第{}行数据校验异常: {}", context.readRowHolder().getRowIndex() + 1, constraintViolationsMsg);
            if (log.isDebugEnabled()) {
                log.error(errMsg);
            }
        }
        excelResult.getErrorList().add(errMsg);
        throw new ExcelAnalysisException(errMsg);
    }

    /**
     * 在解析表头时调用此方法，将表头映射保存下来供后续使用。
     *
     * @param headMap 表头索引与其名称之间的映射关系
     * @param context 分析上下文环境
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.debug("Parse to a header data: {}", JsonUtils.toJsonString(headMap));
    }

    /**
     * 每解析一行数据后都会触发该方法。如果启用了验证功能，则会对当前行数据执行校验逻辑，
     * 校验通过后将其加入结果列表中。
     *
     * @param data    当前行解析得到的对象实例
     * @param context 分析上下文环境
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        if (isValidate) {
            ValidatorUtils.validate(data);
        }
        excelResult.getList().add(data);
    }

    /**
     * 所有数据都解析完成后会回调此方法，在此处可以做一些收尾操作或日志输出等。
     *
     * @param context 分析上下文环境
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("所有数据解析完成！");
    }

    /**
     * 返回本次Excel解析的结果集，包括有效数据列表和错误信息列表。
     *
     * @return 包含解析结果的 {@link ExcelResult} 对象
     */
    @Override
    public ExcelResult<T> getExcelResult() {
        return excelResult;
    }
}
