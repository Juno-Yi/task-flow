package com.junoyi.framework.log.core;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * JunoYi Logger - 统一的日志类
 * 保持传统LoggerFactory.getLogger()使用方式，提供所有增强功能
 *
 * 使用方式：
 * private final JunoLog log = JunoLogFactory.getLogger(Test.class);
 * log.info("普通日志");
 * log.business("user", "login", "success");
 * log.logAsync("INFO", "异步日志");
 *
 * @author Fan
 */
public class JunoYiLog {

    private final Logger delegate;

    public JunoYiLog(Logger delegate) {
        this.delegate = delegate;
    }

    // === 基础日志方法（委托给原始Logger） ===

    public void trace(String msg) {
        delegate.trace(msg);
    }

    public void trace(String format, Object... arguments) {
        delegate.trace(format, arguments);
    }

    public void debug(String msg) {
        delegate.debug(msg);
    }

    public void debug(String format, Object... arguments) {
        delegate.debug(format, arguments);
    }

    public void info(String msg) {
        delegate.info(msg);
    }

    public void info(String format, Object... arguments) {
        delegate.info(format, arguments);
    }

    public void warn(String msg) {
        delegate.warn(msg);
    }

    public void warn(String format, Object... arguments) {
        delegate.warn(format, arguments);
    }

    public void error(String msg) {
        delegate.error(msg);
    }

    public void error(String format, Object... arguments) {
        delegate.error(format, arguments);
    }

    public void error(String msg, Throwable t) {
        delegate.error(msg, t);
    }

    // === 增强功能方法 ===

    /**
     * 带分类的 DEBUG 日志
     */
    public void debug(String category, String msg) {
        delegate.debug("[{}] {}", category, msg);
    }

    /**
     * 带分类和参数的 DEBUG 日志
     */
    public void debug(String category, String format, Object... args) {
        delegate.debug("[{}] " + format, prependCategory(category, args));
    }

    /**
     * 带分类的 INFO 日志
     */
    public void info(String category, String msg) {
        delegate.info("[{}] {}", category, msg);
    }
    
    /**
     * 带分类和参数的 INFO 日志
     */
    public void info(String category, String format, Object... args) {
        delegate.info("[{}] " + format, prependCategory(category, args));
    }
    
    /**
     * 带分类的 WARN 日志
     */
    public void warn(String category, String msg) {
        delegate.warn("[{}] {}", category, msg);
    }
    
    /**
     * 带分类和参数的 WARN 日志
     */
    public void warn(String category, String format, Object... args) {
        delegate.warn("[{}] " + format, prependCategory(category, args));
    }
    
    /**
     * 带分类的 ERROR 日志
     */
    public void error(String category, String msg) {
        delegate.error("[{}] {}", category, msg);
    }
    
    /**
     * 带分类和参数的 ERROR 日志
     */
    public void error(String category, String format, Object... args) {
        delegate.error("[{}] " + format, prependCategory(category, args));
    }
    
    /**
     * 带分类和异常的 ERROR 日志
     */
    public void error(String category, String msg, Throwable t) {
        delegate.error("[{}] {}", category, msg, t);
    }
    
    /**
     * 带分类、参数和异常的 ERROR 日志
     */
    public void error(String category, String format, Throwable t, Object... args) {
        delegate.error("[{}] " + format, prependCategoryWithThrowable(category, t, args));
    }

    /**
     * 辅助方法：将分类添加到参数数组的开头
     */
    private Object[] prependCategory(String category, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = category;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

    /**
     * 辅助方法：将分类和异常添加到参数数组
     */
    private Object[] prependCategoryWithThrowable(String category, Throwable t, Object... args) {
        Object[] newArgs = new Object[args.length + 2];
        newArgs[0] = category;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[newArgs.length - 1] = t;
        return newArgs;
    }

    /**
     * 带分类和上下文的 DEBUG 日志
     */
    public void debug(String category, String msg, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            delegate.debug("[{}] {}", category, msg);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

    /**
     * 带分类和上下文的 INFO 日志
     */
    public void info(String category, String msg, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            delegate.info("[{}] {}", category, msg);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }
    
    /**
     * 带分类和上下文的 WARN 日志
     */
    public void warn(String category, String msg, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            delegate.warn("[{}] {}", category, msg);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }
    
    /**
     * 带分类和上下文的 ERROR 日志
     */
    public void error(String category, String msg, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            delegate.error("[{}] {}", category, msg);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

    /**
     * 在MDC上下文中记录 DEBUG 日志
     */
    public void debugWithMDC(String key, String value, String msg) {
        try {
            MDC.put(key, value);
            debug(msg);
        } finally {
            MDC.remove(key);
        }
    }

    /**
     * 在MDC上下文中记录 DEBUG 日志（多个键值对）
     */
    public void debugWithMDC(Map<String, String> mdcContext, String msg) {
        try {
            if (mdcContext != null) {
                mdcContext.forEach(MDC::put);
            }
            debug(msg);
        } finally {
            if (mdcContext != null) {
                mdcContext.keySet().forEach(MDC::remove);
            }
        }
    }

    /**
     * 在MDC上下文中记录 INFO 日志
     */
    public void infoWithMDC(String key, String value, String msg) {
        try {
            MDC.put(key, value);
            info(msg);
        } finally {
            MDC.remove(key);
        }
    }

    /**
     * 在MDC上下文中记录 INFO 日志（多个键值对）
     */
    public void infoWithMDC(Map<String, String> mdcContext, String msg) {
        try {
            if (mdcContext != null) {
                mdcContext.forEach(MDC::put);
            }
            info(msg);
        } finally {
            if (mdcContext != null) {
                mdcContext.keySet().forEach(MDC::remove);
            }
        }
    }
    
    /**
     * 在MDC上下文中记录 WARN 日志
     */
    public void warnWithMDC(String key, String value, String msg) {
        try {
            MDC.put(key, value);
            warn(msg);
        } finally {
            MDC.remove(key);
        }
    }

    /**
     * 在MDC上下文中记录 WARN 日志（多个键值对）
     */
    public void warnWithMDC(Map<String, String> mdcContext, String msg) {
        try {
            if (mdcContext != null) {
                mdcContext.forEach(MDC::put);
            }
            warn(msg);
        } finally {
            if (mdcContext != null) {
                mdcContext.keySet().forEach(MDC::remove);
            }
        }
    }
    
    /**
     * 在MDC上下文中记录 ERROR 日志
     */
    public void errorWithMDC(String key, String value, String msg) {
        try {
            MDC.put(key, value);
            error(msg);
        } finally {
            MDC.remove(key);
        }
    }

    /**
     * 在MDC上下文中记录 ERROR 日志（多个键值对）
     */
    public void errorWithMDC(Map<String, String> mdcContext, String msg) {
        try {
            if (mdcContext != null) {
                mdcContext.forEach(MDC::put);
            }
            error(msg);
        } finally {
            if (mdcContext != null) {
                mdcContext.keySet().forEach(MDC::remove);
            }
        }
    }

    /**
     * 框架启动日志
     */
    public void startup(String component) {
        info("   JunoYi Framework [{}] 启动成功", component);
    }

    /**
     * 框架关闭日志
     */
    public void shutdown(String component) {
        info("   JunoYi Framework [{}] 已关闭", component);
    }

    /**
     * 性能监控日志
     */
    public void performance(String operation, long duration) {
        info("   性能监控 - 操作: [{}], 耗时: [{}]ms", operation, duration);
    }

    /**
     * 业务日志
     */
    public void business(String module, String action, String result) {
        info("   业务日志 - 模块: [{}], 操作: [{}], 结果: [{}]", module, action, result);
    }

    /**
     * 异常日志（带业务上下文）
     */
    public void businessError(String module, String action, String errorMsg, Throwable t) {
        error("   业务异常 - 模块: [{}], 操作: [{}], 错误: [{}]", module, action, errorMsg, t);
    }

    /**
     * 条件日志 - 仅当条件满足时才记录
     */
    public void infoIf(boolean condition, String msg) {
        if (condition) {
            info(msg);
        }
    }

    /**
     * 条件日志 - 仅当条件满足时才记录格式化日志
     */
    public void infoIf(boolean condition, String format, Object... arguments) {
        if (condition) {
            info(format, arguments);
        }
    }

    // === 异步日志方法 ===

    /**
     * 异步记录日志
     */
    @Async
    public void logAsync(String level, String message, Object... args) {
        switch (level.toUpperCase()) {
            case "INFO":
                info(message, args);
                break;
            case "WARN":
                warn(message, args);
                break;
            case "ERROR":
                error(message, args);
                break;
            case "DEBUG":
                debug(message, args);
                break;
            case "TRACE":
                trace(message, args);
                break;
            default:
                info(message, args);
        }
    }

    /**
     * 异步记录带异常的日志
     */
    @Async
    public void logAsync(String level, String message, Throwable throwable) {
        switch (level.toUpperCase()) {
            case "INFO":
                info(message);
                break;
            case "WARN":
                warn(message);
                break;
            case "ERROR":
                error(message, throwable);
                break;
            case "DEBUG":
                debug(message);
                break;
            case "TRACE":
                trace(message);
                break;
            default:
                error(message, throwable);
        }
    }

    /**
     * 异步性能日志
     */
    @Async
    public void logPerformanceAsync(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        performance(operation, duration);
    }

    /**
     * 异步业务日志
     */
    @Async
    public void logBusinessAsync(String module, String action, String result) {
        business(module, action, result);
    }

    // === MDC工具方法 ===

    /**
     * 在MDC上下文中执行操作
     */
    public <T> T executeWithMDC(Map<String, String> context, Supplier<T> supplier) {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            return supplier.get();
        } finally {
            if (previousContext != null) {
                MDC.setContextMap(previousContext);
            } else {
                MDC.clear();
            }
        }
    }

    /**
     * 在MDC上下文中记录日志
     */
    public void logWithMDC(Map<String, String> context, String level, String message, Object... args) {
        executeWithMDC(context, () -> {
            switch (level.toUpperCase()) {
                case "INFO":
                    info(message, args);
                    break;
                case "WARN":
                    warn(message, args);
                    break;
                case "ERROR":
                    error(message, args);
                    break;
                case "DEBUG":
                    debug(message, args);
                    break;
                case "TRACE":
                    trace(message, args);
                    break;
                default:
                    info(message, args);
            }
            return null;
        });
    }

    /**
     * 生成并设置追踪ID到MDC
     */
    public void setTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("traceId", traceId);
    }

    /**
     * 设置用户信息到MDC
     */
    public void setUserInfo(String userId, String username) {
        if (userId != null) {
            MDC.put("userId", userId);
        }
        if (username != null) {
            MDC.put("username", username);
        }
    }

    /**
     * 设置请求信息到MDC
     */
    public void setRequestInfo(String requestId, String ip, String userAgent) {
        if (requestId != null) {
            MDC.put("requestId", requestId);
        }
        if (ip != null) {
            MDC.put("ip", ip);
        }
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }
    }

    /**
     * 清理MDC上下文
     */
    public void clearMDC() {
        MDC.clear();
    }

    // === 委托原始Logger的方法 ===

    public String getName() {
        return delegate.getName();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    public Logger getDelegate() {
        return delegate;
    }
}
