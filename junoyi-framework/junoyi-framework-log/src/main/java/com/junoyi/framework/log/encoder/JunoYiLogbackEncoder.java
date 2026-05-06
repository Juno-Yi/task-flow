package com.junoyi.framework.log.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;
import com.junoyi.framework.log.terminal.TerminalColor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 框架日志编码器
 * 提供美观的彩色日志输出格式，支持MDC上下文和线程信息
 *
 * @author Fan
 */
public class JunoYiLogbackEncoder extends EncoderBase<ILoggingEvent> {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean showThreadName = true;
    private boolean showMDC = true;
    private boolean showClassName = true;
    private int maxClassNameLength = 20;
    private boolean colorEnabled = true;
    private int maxStackTraceLines = 8;
    private int maxMdcProperties = 3;
    private boolean simplifyPackageNames = true;
    private java.util.Map<String, String> packageSimplifications = new java.util.LinkedHashMap<>();
    private int maxThreadNameLength = 15;

    @Override
    public byte[] encode(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder(256);
        
        // 颜色配置
        String reset = colorEnabled ? TerminalColor.RESET : "";
        String redColor = colorEnabled ? TerminalColor.RED : "";
        String greenColor = colorEnabled ? TerminalColor.GREEN : "";
        String purpleColor = colorEnabled ? TerminalColor.PURPLE : "";
        String cyanColor = colorEnabled ? TerminalColor.CYAN : "";
        
        // 时间戳（红色，更精确的格式）
        sb.append(redColor)
                .append("[")
                .append(formatTimestamp(event.getTimeStamp()))
                .append("] ")
                .append(reset);

        // 线程名（绿色，带括号，固定12字符宽度）
        if (showThreadName) {
            String threadName = formatThread(event.getThreadName());
            sb.append(greenColor)
                    .append("(")
                    .append(String.format("%-" + Math.max(1, maxThreadNameLength) + "s", threadName))
                    .append(") ")
                    .append(reset);
        }

        // Logger名称（青色，智能缩进，固定30字符宽度）
        String loggerName = event.getLoggerName();
        if (showClassName) {
            String formattedLogger = formatLoggerNameAdvanced(loggerName);
            sb.append(cyanColor)
                    .append(String.format("%-30s", formattedLogger))
                    .append(reset)
                    .append(" ");
        }

        // MDC上下文（紫色，如果有）
        if (showMDC && !event.getMDCPropertyMap().isEmpty()) {
            sb.append(purpleColor)
                    .append("[MDC: ")
                    .append(formatMDC(event.getMDCPropertyMap()))
                    .append("] ")
                    .append(reset);
        }

        // 日志级别（彩色背景，固定7字符宽度，文字居中）
        String levelText = event.getLevel().toString();
        String levelStr;
        switch (levelText.length()) {
            case 4: // INFO
                levelStr = "[" + levelText + "] ";
                break;
            case 5: // ERROR, DEBUG, TRACE
                levelStr = "[" + levelText + "]";
                break;
            case 3: // WARN
                levelStr = "[WARN ] ";
                break;
            default:
                levelStr = "[" + levelText + "]";
                break;
        }
        
        String levelColor = getLevelColor(event.getLevel());
        sb.append(colorEnabled ? levelColor : "")
                .append(String.format("%7s", levelStr))
                .append(reset);

        // 日志消息（根据级别着色，另起一行）
        sb.append("\n  - ");
        String messageColor = getMessageColor(event.getLevel());
        sb.append(colorEnabled ? messageColor : "")
                .append(event.getFormattedMessage())
                .append(reset);

        // 异常信息（红色，带缩进）
        if (event.getThrowableProxy() != null) {
            sb.append("\n").append(formatThrowableAdvanced(event.getThrowableProxy()));
        }

        sb.append("\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 根据日志级别获取颜色
     */
    private String getLevelColor(ch.qos.logback.classic.Level level) {
        if (!colorEnabled) {
            return "";
        }
        
        switch (level.toInt()) {
            case ch.qos.logback.classic.Level.ERROR_INT:
                return TerminalColor.BOLD_WHITE_ON_RED;
            case ch.qos.logback.classic.Level.WARN_INT:
                return TerminalColor.BOLD_BLACK_ON_YELLOW;
            case ch.qos.logback.classic.Level.INFO_INT:
                return TerminalColor.BOLD_BLACK_ON_GREEN;
            case ch.qos.logback.classic.Level.DEBUG_INT:
                return TerminalColor.BOLD_WHITE_ON_BLUE;
            case ch.qos.logback.classic.Level.TRACE_INT:
                return TerminalColor.BOLD_WHITE_ON_PURPLE;
            default:
                return TerminalColor.GREEN;
        }
    }

    /**
     * 根据日志级别获取消息颜色
     */
    private String getMessageColor(ch.qos.logback.classic.Level level) {
        if (!colorEnabled) {
            return "";
        }
        
        switch (level.toInt()) {
            case ch.qos.logback.classic.Level.ERROR_INT:
                return TerminalColor.BOLD_RED;
            case ch.qos.logback.classic.Level.WARN_INT:
                return TerminalColor.BOLD_YELLOW;
            case ch.qos.logback.classic.Level.INFO_INT:
                return ""; // INFO级别使用默认样式，不重置颜色
            case ch.qos.logback.classic.Level.DEBUG_INT:
                return TerminalColor.BOLD_BLUE;
            case ch.qos.logback.classic.Level.TRACE_INT:
                return TerminalColor.BOLD_PURPLE;
            default:
                return TerminalColor.WHITE;
        }
    }

    /**
     * 高级Logger名称格式化
     */
    private String formatLoggerNameAdvanced(String loggerName) {
        if (loggerName == null) {
            return "";
        }
        String base = applyPackageSimplifications(loggerName);
        if (simplifyPackageNames) {
            String tmp = base;
            if (tmp.length() > 28) {
                String[] parts = tmp.split("\\.");
                if (parts.length > 2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < parts.length - 1; i++) {
                        if (parts[i].length() > 0) {
                            sb.append(parts[i].charAt(0)).append(".");
                        }
                    }
                    sb.append(parts[parts.length - 1]);
                    tmp = sb.toString();
                }
            }
            base = tmp;
        }
        if (base.length() > 28) {
            base = base.substring(0, 25) + "...";
        }
        return base;
    }

    /**
     * 高级异常信息格式化
     */
    private String formatThrowableAdvanced(ch.qos.logback.classic.spi.IThrowableProxy throwableProxy) {
        if (throwableProxy == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        String redColor = colorEnabled ? TerminalColor.RED : "";
        String yellowColor = colorEnabled ? TerminalColor.YELLOW : "";
        String reset = colorEnabled ? TerminalColor.RESET : "";
        
        sb.append(redColor)
                .append("┌─ 异常堆栈跟踪 ")
                .append("─".repeat(60))
                .append("\n")
                .append("│ ")
                .append(yellowColor)
                .append(throwableProxy.getClassName())
                .append(": ")
                .append(throwableProxy.getMessage())
                .append(reset)
                .append("\n");
        
        ch.qos.logback.classic.spi.StackTraceElementProxy[] steArray = throwableProxy.getStackTraceElementProxyArray();
        int limit = maxStackTraceLines <= 0 ? steArray.length : Math.min(steArray.length, maxStackTraceLines);
        for (int i = 0; i < limit; i++) {
            sb.append(redColor).append("│ ").append(reset)
                    .append("   at ")
                    .append(colorEnabled ? TerminalColor.CYAN : "")
                    .append(steArray[i].getSTEAsString())
                    .append(reset)
                    .append("\n");
        }
        
        if (maxStackTraceLines > 0 && steArray.length > maxStackTraceLines) {
            sb.append(redColor)
                    .append("│ ")
                    .append(yellowColor)
                    .append("   ... ")
                    .append(steArray.length - maxStackTraceLines)
                    .append(" more")
                    .append(reset)
                    .append("\n");
        }
        
        sb.append(redColor)
                .append("└─")
                .append("─".repeat(75))
                .append(reset);
        
        return sb.toString();
    }

    /**
     * 格式化时间戳
     */
    private String formatTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp), 
            java.time.ZoneId.systemDefault()
        ).format(dateFormatter);
    }

    /**
     * 格式化线程名
     */
    private String formatThread(String threadName) {
        if (threadName == null) {
            return "unknown";
        }
        // 简化常见线程名
        switch (threadName) {
            case "main": return "main";
            case "restartedMain": return "main";
            default:
                int width = Math.max(4, maxThreadNameLength);
                if (threadName.length() > width) {
                    int tail = Math.max(1, width - 3);
                    return "..." + threadName.substring(threadName.length() - tail);
                }
                return threadName;
        }
    }

    /**
     * 格式化MDC上下文
     */
    private String formatMDC(java.util.Map<String, String> mdcMap) {
        if (mdcMap == null || mdcMap.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        mdcMap.entrySet().stream()
                .limit(Math.max(0, maxMdcProperties))
                .forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append(","));
        
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1); // 移除最后的逗号
        }
        sb.append("]");
        return sb.toString();
    }

    // === Getter/Setter方法 ===
    
    public boolean isShowThreadName() {
        return showThreadName;
    }

    public void setShowThreadName(boolean showThreadName) {
        this.showThreadName = showThreadName;
    }

    public boolean isShowMDC() {
        return showMDC;
    }

    public void setShowMDC(boolean showMDC) {
        this.showMDC = showMDC;
    }

    public boolean isShowClassName() {
        return showClassName;
    }

    public void setShowClassName(boolean showClassName) {
        this.showClassName = showClassName;
    }

    public int getMaxClassNameLength() {
        return maxClassNameLength;
    }

    public void setMaxClassNameLength(int maxClassNameLength) {
        this.maxClassNameLength = Math.max(10, maxClassNameLength);
    }

    public boolean isColorEnabled() {
        return colorEnabled;
    }

    public void setColorEnabled(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }

    public void setDateTimePattern(String pattern) {
        if (pattern != null && !pattern.isEmpty()) {
            this.dateFormatter = DateTimeFormatter.ofPattern(pattern);
        }
    }

    public void setMaxStackTraceLines(int maxStackTraceLines) {
        this.maxStackTraceLines = maxStackTraceLines;
    }

    public void setMaxMdcProperties(int maxMdcProperties) {
        this.maxMdcProperties = maxMdcProperties;
    }

    public void setSimplifyPackageNames(boolean simplifyPackageNames) {
        this.simplifyPackageNames = simplifyPackageNames;
    }

    public void setPackageSimplifications(String mapping) {
        if (mapping == null || mapping.trim().isEmpty()) {
            return;
        }
        String[] pairs = mapping.split(",");
        for (String p : pairs) {
            String[] kv = p.split(":");
            if (kv.length == 2) {
                packageSimplifications.put(kv[0].trim(), kv[1].trim());
            }
        }
    }

    public void setMaxThreadNameLength(int maxThreadNameLength) {
        this.maxThreadNameLength = maxThreadNameLength;
    }

    private String applyPackageSimplifications(String loggerName) {
        if (loggerName == null) {
            return "";
        }
        String result = loggerName;
        if (simplifyPackageNames && !packageSimplifications.isEmpty()) {
            for (java.util.Map.Entry<String, String> e : packageSimplifications.entrySet()) {
                String key = e.getKey();
                String val = e.getValue();
                if (result.startsWith(key + ".")) {
                    result = val + result.substring(key.length());
                } else if (result.equals(key)) {
                    result = val;
                }
            }
        }
        return result;
    }
    @Override
    public byte[] headerBytes() { 
        return null; 
    }
    
    @Override
    public byte[] footerBytes() { 
        return null; 
    }
}
