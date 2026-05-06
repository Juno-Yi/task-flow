package com.junoyi.framework.json.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.junoyi.framework.core.utils.SpringUtils;
import com.junoyi.framework.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Json 工具类
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {
    /**
     * 全局共享的 ObjectMapper 实例，通过 Spring 获取。
     */
    private static final ObjectMapper OBJECT_MAPPER = SpringUtils.getBean(ObjectMapper.class);

    /**
     * 获取全局共享的 ObjectMapper 实例。
     *
     * @return ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param object 待序列化的对象，可以为 null
     * @return 序列化后的 JSON 字符串；如果输入为 null，则返回 null
     * @throws RuntimeException 当序列化失败时抛出运行时异常
     */
    public static String toJsonString(Object object) {
        if (ObjectUtil.isNull(object))
            return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型 Class 对象
     * @param <T>   泛型参数，表示目标类型
     * @return 反序列化后的对象；如果输入字符串为空则返回 null
     * @throws RuntimeException 当反序列化失败时抛出运行时异常
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text))
            return null;
        try {
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节数组反序列化为指定类型的对象。
     *
     * @param bytes 字节数组
     * @param clazz 目标类型 Class 对象
     * @param <T>   泛型参数，表示目标类型
     * @return 反序列化后的对象；如果输入数组为空则返回 null
     * @throws RuntimeException 当反序列化失败时抛出运行时异常
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes))
            return null;
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 TypeReference 将 JSON 字符串反序列化为复杂泛型结构的对象。
     *
     * @param text          JSON 字符串
     * @param typeReference 描述目标类型的 TypeReference 对象
     * @param <T>           泛型参数，表示目标类型
     * @return 反序列化后的对象；如果输入字符串为空则返回 null
     * @throws RuntimeException 当反序列化失败时抛出运行时异常
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text))
            return null;
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串解析为 Dict 类型的 Map 结构。
     *
     * @param text JSON 字符串
     * @return 解析得到的 Dict 对象；如果输入为空或格式错误则返回 null
     * @throws RuntimeException 当发生 IO 异常且不是类型不匹配导致时抛出运行时异常
     */
    public static Dict parseMap(String text) {
        if (StringUtils.isBlank(text))
            return null;
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructType(Dict.class));
        } catch (MismatchedInputException e) {
            // 类型不匹配说明不是json
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 数组字符串解析为 List<Dict> 结构。
     *
     * @param text JSON 数组字符串
     * @return 解析后的 List<Dict> 列表；如果输入为空则返回 null
     * @throws RuntimeException 当解析失败时抛出运行时异常
     */
    public static List<Dict> parseArrayMap(String text) {
        if (StringUtils.isBlank(text))
            return null;
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, Dict.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 数组字符串解析为指定元素类型的 List。
     *
     * @param text  JSON 数组字符串
     * @param clazz 列表中元素的目标类型 Class 对象
     * @param <T>   泛型参数，表示列表中的元素类型
     * @return 解析后的 List<T> 列表；如果输入为空则返回空列表（非 null）
     * @throws RuntimeException 当解析失败时抛出运行时异常
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text))
            return new ArrayList<>();
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
