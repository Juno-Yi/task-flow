package com.junoyi.framework.json.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

/**
 * BigNumberSerializer 类用于序列化超出 JavaScript 安全整数范围的数字
 * 该类继承自 NumberSerializer，专门处理大数字的 JSON 序列化
 *
 * @author Fan
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

    /**
     * 根据 JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER 得来
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 提供实例对象
     */
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

    /**
     * 构造函数，创建 BigNumberSerializer 实例
     * @param rawType 要序列化的数字类型的 Class 对象
     */
    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    /**
     * 序列化数字对象为 JSON 格式
     * 如果数字在 JavaScript 安全整数范围内，则使用父类的序列化方法
     * 如果数字超出安全范围，则将其序列化为字符串格式
     * @param value 要序列化的数字对象
     * @param gen JSON 生成器，用于输出序列化结果
     * @param provider 序列化提供者，包含序列化配置信息
     * @throws IOException 当序列化过程中发生 IO 错误时抛出
     */
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 超出范围 序列化位字符串
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER)
            super.serialize(value, gen, provider);
        else
            gen.writeString(value.toString());

    }
}
