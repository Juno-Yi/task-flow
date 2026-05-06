package com.junoyi.framework.log.core;

import org.slf4j.LoggerFactory;

/**
 * JunoYi Logger工厂类
 * 提供与传统LoggerFactory相同的API，但返回JunoLog实例
 * 
 * 使用方式：
 * private final JunoLog log = JunoLogFactory.getLogger(Test.class);
 * log.info("普通日志");
 * log.business("user", "login", "success");
 * 
 * @author Fan
 */
public class JunoYiLogFactory {
    
    /**
     * 获取指定类的增强Logger实例
     */
    public static JunoYiLog getLogger(Class<?> clazz) {
        return new JunoYiLog(LoggerFactory.getLogger(clazz));
    }
    
    /**
     * 获取指定名称的增强Logger实例
     */
    public static JunoYiLog getLogger(String name) {
        return new JunoYiLog(LoggerFactory.getLogger(name));
    }
    
    /**
     * 从原始Logger创建增强Logger
     */
    public static JunoYiLog wrap(org.slf4j.Logger logger) {
        return new JunoYiLog(logger);
    }
}
