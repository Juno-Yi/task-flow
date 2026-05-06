package com.junoyi.framework.web.xss;

import com.junoyi.framework.core.utils.StringUtils;

import java.util.regex.Pattern;

/**
 * XSS 过滤工具类
 *
 * @author Fan
 */
public class XssUtils {

    private XssUtils() {}

    // 预编译正则表达式，提升性能
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*?>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern SCRIPT_TAG_START = Pattern.compile("<script[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCRIPT_TAG_END = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_PATTERN = Pattern.compile("<style[^>]*?>.*?</style>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern IFRAME_PATTERN = Pattern.compile("<iframe[^>]*?>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern FRAME_PATTERN = Pattern.compile("<frame[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern FRAMESET_PATTERN = Pattern.compile("<frameset[^>]*?>.*?</frameset>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern OBJECT_PATTERN = Pattern.compile("<object[^>]*?>.*?</object>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EMBED_PATTERN = Pattern.compile("<embed[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern APPLET_PATTERN = Pattern.compile("<applet[^>]*?>.*?</applet>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern META_PATTERN = Pattern.compile("<meta[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern LINK_PATTERN = Pattern.compile("<link[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASE_PATTERN = Pattern.compile("<base[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern FORM_PATTERN = Pattern.compile("<form[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern INPUT_HIDDEN_PATTERN = Pattern.compile("<input[^>]*type\\s*=\\s*[\"']?hidden[\"']?[^>]*>", Pattern.CASE_INSENSITIVE);

    // 事件处理器
    private static final Pattern ON_EVENT_PATTERN = Pattern.compile("\\s+on\\w+\\s*=\\s*([\"'][^\"']*[\"']|[^\\s>]+)", Pattern.CASE_INSENSITIVE);

    // 危险协议
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA_PATTERN = Pattern.compile("data\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIVESCRIPT_PATTERN = Pattern.compile("livescript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern MOCHA_PATTERN = Pattern.compile("mocha\\s*:", Pattern.CASE_INSENSITIVE);

    // CSS 表达式
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_PATTERN = Pattern.compile("url\\s*\\(\\s*[\"']?\\s*javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEHAVIOR_PATTERN = Pattern.compile("behavior\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern BINDING_PATTERN = Pattern.compile("-moz-binding\\s*:", Pattern.CASE_INSENSITIVE);

    // SVG 相关
    private static final Pattern SVG_PATTERN = Pattern.compile("<svg[^>]*?>.*?</svg>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern SVG_ONLOAD_PATTERN = Pattern.compile("<svg[^>]*onload[^>]*>", Pattern.CASE_INSENSITIVE);

    // 特殊编码绕过
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u00[0-9a-fA-F]{2}");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#x[0-9a-fA-F]+;?");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("&#\\d+;?");

    /**
     * 过滤 XSS 脚本内容（严格模式）
     *
     * @param value 待处理内容
     * @return 过滤后的内容
     */
    public static String clean(String value) {
        if (StringUtils.isBlank(value)) return value;

        // 先解码可能的编码绕过
        value = decodeSpecialChars(value);

        // 移除危险标签
        value = SCRIPT_PATTERN.matcher(value).replaceAll("");
        value = SCRIPT_TAG_START.matcher(value).replaceAll("");
        value = SCRIPT_TAG_END.matcher(value).replaceAll("");
        value = STYLE_PATTERN.matcher(value).replaceAll("");
        value = IFRAME_PATTERN.matcher(value).replaceAll("");
        value = FRAME_PATTERN.matcher(value).replaceAll("");
        value = FRAMESET_PATTERN.matcher(value).replaceAll("");
        value = OBJECT_PATTERN.matcher(value).replaceAll("");
        value = EMBED_PATTERN.matcher(value).replaceAll("");
        value = APPLET_PATTERN.matcher(value).replaceAll("");
        value = META_PATTERN.matcher(value).replaceAll("");
        value = LINK_PATTERN.matcher(value).replaceAll("");
        value = BASE_PATTERN.matcher(value).replaceAll("");
        value = SVG_PATTERN.matcher(value).replaceAll("");

        // 移除事件处理器
        value = ON_EVENT_PATTERN.matcher(value).replaceAll("");

        // 移除危险协议
        value = JAVASCRIPT_PATTERN.matcher(value).replaceAll("");
        value = VBSCRIPT_PATTERN.matcher(value).replaceAll("");
        value = DATA_PATTERN.matcher(value).replaceAll("");
        value = LIVESCRIPT_PATTERN.matcher(value).replaceAll("");
        value = MOCHA_PATTERN.matcher(value).replaceAll("");

        // 移除 CSS 表达式
        value = EXPRESSION_PATTERN.matcher(value).replaceAll("");
        value = URL_PATTERN.matcher(value).replaceAll("url(");
        value = BEHAVIOR_PATTERN.matcher(value).replaceAll("");
        value = BINDING_PATTERN.matcher(value).replaceAll("");

        return value;
    }

    /**
     * 转义 HTML 特殊字符（用于输出时转义）
     *
     * @param value 待处理内容
     * @return 转义后的内容
     */
    public static String escape(String value) {
        if (StringUtils.isBlank(value)) return value;

        StringBuilder sb = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                case '/' -> sb.append("&#47;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 判断是否包含 XSS 攻击内容
     *
     * @param value 待检测内容
     * @return true 包含 XSS 内容
     */
    public static boolean containsXss(String value) {
        if (StringUtils.isBlank(value)) return false;

        // 先解码
        String decoded = decodeSpecialChars(value);

        // 检测危险标签
        if (SCRIPT_PATTERN.matcher(decoded).find()) return true;
        if (SCRIPT_TAG_START.matcher(decoded).find()) return true;
        if (IFRAME_PATTERN.matcher(decoded).find()) return true;
        if (OBJECT_PATTERN.matcher(decoded).find()) return true;
        if (EMBED_PATTERN.matcher(decoded).find()) return true;
        if (SVG_ONLOAD_PATTERN.matcher(decoded).find()) return true;

        // 检测事件处理器
        if (ON_EVENT_PATTERN.matcher(decoded).find()) return true;

        // 检测危险协议
        if (JAVASCRIPT_PATTERN.matcher(decoded).find()) return true;
        if (VBSCRIPT_PATTERN.matcher(decoded).find()) return true;
        if (DATA_PATTERN.matcher(decoded).find()) return true;

        // 检测 CSS 表达式
        if (EXPRESSION_PATTERN.matcher(decoded).find()) return true;

        return false;
    }

    /**
     * 解码特殊字符（防止编码绕过）
     */
    private static String decodeSpecialChars(String value) {
        if (value == null) return null;

        // 解码 HTML 实体
        value = value.replace("&lt;", "<");
        value = value.replace("&gt;", ">");
        value = value.replace("&quot;", "\"");
        value = value.replace("&#39;", "'");
        value = value.replace("&amp;", "&");

        // 解码十六进制 HTML 实体 (&#xNN;)
        java.util.regex.Matcher hexMatcher = HEX_PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group().replaceAll("[&#x;]", "");
            try {
                int code = Integer.parseInt(hex, 16);
                hexMatcher.appendReplacement(sb, String.valueOf((char) code));
            } catch (NumberFormatException e) {
                // 忽略无效的编码
            }
        }
        hexMatcher.appendTail(sb);
        value = sb.toString();

        // 解码十进制 HTML 实体 (&#NN;)
        java.util.regex.Matcher decMatcher = DECIMAL_PATTERN.matcher(value);
        sb = new StringBuffer();
        while (decMatcher.find()) {
            String dec = decMatcher.group().replaceAll("[&#;]", "");
            try {
                int code = Integer.parseInt(dec);
                decMatcher.appendReplacement(sb, String.valueOf((char) code));
            } catch (NumberFormatException e) {
                // 忽略无效的编码
            }
        }
        decMatcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 清理 JSON 字符串中的 XSS
     *
     * @param json JSON 字符串
     * @return 清理后的 JSON
     */
    public static String cleanJson(String json) {
        if (StringUtils.isBlank(json)) return json;
        // JSON 中的值需要特殊处理，避免破坏 JSON 结构
        return clean(json);
    }
}
