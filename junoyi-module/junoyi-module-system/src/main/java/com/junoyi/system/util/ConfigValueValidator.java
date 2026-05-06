package com.junoyi.system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.system.enums.ConfigType;
import com.junoyi.system.exception.ConfigTypeInvalidException;
import com.junoyi.system.exception.ConfigValueInvalidException;

/**
 * 配置值验证工具类
 *
 * @author Fan
 */
public class ConfigValueValidator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 验证配置值是否符合指定类型
     *
     * @param configType  配置类型
     * @param configValue 配置值
     * @throws ConfigTypeInvalidException  如果配置类型无效
     * @throws ConfigValueInvalidException 如果配置值验证失败
     */
    public static void validate(String configType, String configValue) {
        if (StringUtils.isBlank(configValue)) {
            return; // 允许空值
        }

        ConfigType type = ConfigType.fromCode(configType);
        if (type == null) {
            throw new ConfigTypeInvalidException(configType);
        }

        switch (type) {
            case NUMBER:
                validateNumber(configValue);
                break;
            case BOOLEAN:
                validateBoolean(configValue);
                break;
            case JSON:
                validateJson(configValue);
                break;
            case TEXT:
            default:
                // 文本类型不需要特殊验证
                break;
        }
    }

    /**
     * 验证数字类型
     */
    private static void validateNumber(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConfigValueInvalidException("配置值必须是有效的数字类型");
        }
    }

    /**
     * 验证布尔类型
     */
    private static void validateBoolean(String value) {
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new ConfigValueInvalidException("配置值必须是 true 或 false");
        }
    }

    /**
     * 验证JSON类型
     */
    private static void validateJson(String value) {
        try {
            OBJECT_MAPPER.readTree(value);
        } catch (Exception e) {
            throw new ConfigValueInvalidException("配置值必须是有效的 JSON 格式: " + e.getMessage());
        }
    }
}
