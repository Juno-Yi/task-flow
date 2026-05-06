package com.junoyi.framework.redis.handler;

import com.junoyi.framework.core.utils.StringUtils;
import org.redisson.api.NameMapper;

/**
 * redis缓存key前缀处理
 *
 * @author Fan
 */
public class KeyPrefixHandler implements NameMapper {

    private final String keyPrefix;

    /**
     * 构造函数，初始化key前缀处理器
     *
     * @param keyPrefix Redis键的前缀字符串，如果为空或空白字符则不使用前缀
     */
    public KeyPrefixHandler(String keyPrefix){
        // 如果前缀为空白字符，则设置为空字符串；否则设置为前缀加冒号格式
        this.keyPrefix = StringUtils.isBlank(keyPrefix) ? "" : keyPrefix + ":";
    }

    /**
     * 为Redis键名添加前缀
     *
     * @param name 原始键名
     * @return 添加前缀后的键名，如果输入为空则返回null
     */
    @Override
    public String map(String name) {
        // 检查输入键名是否为空
        if (StringUtils.isBlank(name))
            return null;
        // 如果已配置前缀且键名不以该前缀开头，则添加前缀
        if (StringUtils.isNotBlank(keyPrefix) && !name.startsWith(keyPrefix))
            return keyPrefix + name;
        return name;
    }

    /**
     * 从Redis键名中移除前缀
     *
     * @param name 带前缀的键名
     * @return 移除前缀后的原始键名，如果输入为空则返回null
     */
    @Override
    public String unmap(String name) {
        // 检查输入键名是否为空
        if (StringUtils.isBlank(name))
            return null;
        // 如果已配置前缀且键名以该前缀开头，则移除前缀
        if (StringUtils.isNotBlank(keyPrefix) && name.startsWith(keyPrefix))
            return name.substring(keyPrefix.length());
        return name;
    }

}
