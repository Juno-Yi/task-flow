package com.junoyi.framework.web.sql;

import com.junoyi.framework.core.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL 注入检测工具类
 * <p>
 * 优化策略：
 * 1. 只检测真正危险的 SQL 注入模式，避免误报
 * 2. 单个关键词（如 delete、select）不触发拦截
 * 3. 只有关键词 + 危险语法结构才触发拦截
 *
 * @author Fan
 */
public class SqlInjectionUtils {

    private SqlInjectionUtils() {}

    /**
     * 高危 SQL 关键词（需要配合危险语法才触发）
     */
    private static final Set<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList(
            "select", "insert", "update", "delete", "drop", "truncate", "alter",
            "create", "exec", "execute", "union", "declare", "shutdown", "grant", "revoke"
    ));

    /**
     * 危险模式 - 真正的 SQL 注入攻击特征
     */
    private static final Pattern[] DANGEROUS_PATTERNS = {
            // SQL 注释攻击（必须有前置内容）
            Pattern.compile(".+--\\s*$"),
            Pattern.compile(".+/\\*.*?\\*/", Pattern.DOTALL),
            
            // 经典单引号注入
            Pattern.compile("'\\s*(or|and)\\s*'", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'\\s*(or|and)\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'\\s*(or|and)\\s+\\w+\\s*=\\s*\\w+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'\\s*;", Pattern.CASE_INSENSITIVE),
            
            // 永真条件（必须有引号或特殊上下文）
            Pattern.compile("'\\s*\\d+\\s*'\\s*=\\s*'\\s*\\d+\\s*'"),
            Pattern.compile("\\bor\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\band\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
            
            // UNION 注入
            Pattern.compile("union\\s+(all\\s+)?select", Pattern.CASE_INSENSITIVE),
            
            // 堆叠查询（分号后跟 SQL 语句）
            Pattern.compile(";\\s*(select|insert|update|delete|drop|truncate|alter|create)\\b", Pattern.CASE_INSENSITIVE),
            
            // 时间盲注
            Pattern.compile("sleep\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("benchmark\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("waitfor\\s+delay", Pattern.CASE_INSENSITIVE),
            Pattern.compile("pg_sleep\\s*\\(", Pattern.CASE_INSENSITIVE),
            
            // 报错注入
            Pattern.compile("extractvalue\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("updatexml\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("exp\\s*\\(\\s*~", Pattern.CASE_INSENSITIVE),
            
            // 系统函数调用
            Pattern.compile("load_file\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("into\\s+(outfile|dumpfile)", Pattern.CASE_INSENSITIVE),
            
            // 存储过程调用
            Pattern.compile("\\bxp_\\w+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bsp_\\w+", Pattern.CASE_INSENSITIVE),
            
            // 十六进制编码的 SQL（长度 > 10 才检测，避免误报短 hex）
            Pattern.compile("0x[0-9a-fA-F]{10,}"),
            
            // 字符串拼接攻击
            Pattern.compile("concat\\s*\\(.*select", Pattern.CASE_INSENSITIVE),
            Pattern.compile("char\\s*\\(\\s*\\d+\\s*(,\\s*\\d+\\s*)+\\)", Pattern.CASE_INSENSITIVE)
    };

    /**
     * 自定义危险模式（可通过配置追加）
     */
    private static Set<Pattern> customPatterns = new HashSet<>();

    /**
     * 设置自定义关键词（转换为模式）
     */
    public static void setCustomKeywords(Set<String> keywords) {
        customPatterns.clear();
        for (String keyword : keywords) {
            // 自定义关键词需要配合危险语法才触发
            customPatterns.add(Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b.*[';\\-]", Pattern.CASE_INSENSITIVE));
        }
    }

    /**
     * 检测是否包含 SQL 注入
     * <p>
     * 优化后的检测策略：
     * 1. 先检测明确的危险模式（高置信度）
     * 2. 再检测关键词 + 危险语法组合
     * 3. 单独的关键词（如 delete、select）不触发拦截
     *
     * @param value 待检测内容
     * @return true 包含 SQL 注入
     */
    public static boolean containsSqlInjection(String value) {
        if (StringUtils.isBlank(value)) return false;

        // 检测明确的危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(value).find()) return true;
        }

        // 检测危险的关键词组合（多个 SQL 关键词 + 危险语法）
        if (containsDangerousKeywordCombination(value)) return true;

        // 检测自定义模式
        for (Pattern pattern : customPatterns) {
            if (pattern.matcher(value).find()) return true;
        }

        return false;
    }

    /**
     * 安全的标识符模式 - 这些格式的关键词不应触发检测
     * 如：system.api.user.delete、permission.update 等
     */
    private static final Pattern SAFE_IDENTIFIER_PATTERN = Pattern.compile(
            "\\w+\\.\\w+\\.(\\w+\\.)*(select|insert|update|delete|drop|truncate|alter|create|exec|execute|union|declare|grant|revoke)(\\.[\\w-]+)*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 检测是否包含危险的 SQL 关键词组合
     * <p>
     * 优化策略：
     * - 需要 2 个以上 SQL 关键词同时出现
     * - 或者 1 个关键词 + 明确的注入语法（如 ' OR、; DROP 等）
     * - 点分隔的标识符格式（如 system.api.user.delete）不触发
     */
    private static boolean containsDangerousKeywordCombination(String value) {
        // 先移除安全的标识符格式，避免误报
        String cleanedValue = removeSafeIdentifiers(value);
        
        // 统计出现的 SQL 关键词（在清理后的内容中）
        int keywordCount = 0;
        Set<String> foundKeywords = new HashSet<>();
        
        for (String keyword : SQL_KEYWORDS) {
            if (Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(cleanedValue).find()) {
                keywordCount++;
                foundKeywords.add(keyword);
            }
        }

        // 没有关键词，安全
        if (keywordCount == 0) return false;

        // 多个关键词 + 任意危险字符
        if (keywordCount >= 2) {
            // 检查是否有危险语法结构
            if (hasDangerousSyntax(cleanedValue)) {
                return true;
            }
            // 特殊组合：select + from / insert + into / delete + from 等
            if (hasDangerousKeywordPair(foundKeywords, cleanedValue)) {
                return true;
            }
        }

        // 单个关键词需要配合明确的注入语法
        if (keywordCount == 1) {
            return hasClearInjectionSyntax(cleanedValue, foundKeywords.iterator().next());
        }

        return false;
    }

    /**
     * 移除安全的标识符格式
     * 将 system.api.user.delete 这类格式替换为占位符，避免误报
     */
    private static String removeSafeIdentifiers(String value) {
        // 移除点分隔的标识符（如 system.api.user.delete、permission.update）
        String result = SAFE_IDENTIFIER_PATTERN.matcher(value).replaceAll("__SAFE_ID__");
        
        // 移除 JSON 字段名中的关键词（如 "delete": true, "updateTime": "xxx"）
        // 匹配 "keyword" 或 keyword: 格式
        for (String keyword : SQL_KEYWORDS) {
            // "delete": 或 "delete" : 格式
            result = result.replaceAll("\"" + keyword + "\"\\s*:", "__SAFE_FIELD__:");
            // deleteTime、updateBy 等驼峰命名
            result = result.replaceAll("(?i)\"\\w*" + keyword + "\\w*\"\\s*:", "__SAFE_FIELD__:");
        }
        
        return result;
    }

    /**
     * 检测是否有危险语法结构
     */
    private static boolean hasDangerousSyntax(String value) {
        // 单引号 + 逻辑运算符
        if (Pattern.compile("'\\s*(or|and|;|--|#)", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // 分号分隔的多语句
        if (Pattern.compile(";\\s*\\w+\\s+\\w+", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        return false;
    }

    /**
     * 检测危险的关键词配对
     */
    private static boolean hasDangerousKeywordPair(Set<String> keywords, String value) {
        // select ... from
        if (keywords.contains("select") && 
            Pattern.compile("\\bselect\\b.+\\bfrom\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // insert ... into
        if (keywords.contains("insert") && 
            Pattern.compile("\\binsert\\b.+\\binto\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // delete ... from
        if (keywords.contains("delete") && 
            Pattern.compile("\\bdelete\\b.+\\bfrom\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // update ... set
        if (keywords.contains("update") && 
            Pattern.compile("\\bupdate\\b.+\\bset\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // drop/truncate + table
        if ((keywords.contains("drop") || keywords.contains("truncate")) && 
            Pattern.compile("\\b(drop|truncate)\\b.+\\btable\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        return false;
    }

    /**
     * 检测单个关键词是否配合了明确的注入语法
     */
    private static boolean hasClearInjectionSyntax(String value, String keyword) {
        // 关键词前有单引号闭合
        if (Pattern.compile("'\\s*" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // 关键词前有分号（堆叠查询）
        if (Pattern.compile(";\\s*" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        // 关键词后跟危险语法
        if (Pattern.compile("\\b" + keyword + "\\b\\s*['\";#]", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
            return true;
        }
        return false;
    }

    /**
     * 清理 SQL 注入内容
     *
     * @param value 待处理内容
     * @return 清理后的内容
     */
    public static String clean(String value) {
        if (StringUtils.isBlank(value)) return value;

        // 移除 SQL 注释
        value = value.replaceAll("--.*", "");
        value = value.replaceAll("/\\*.*?\\*/", "");

        // 转义单引号
        value = value.replace("'", "''");

        // 移除分号（防止堆叠查询）
        value = value.replace(";", "");

        // 移除危险函数调用
        value = value.replaceAll("(?i)sleep\\s*\\([^)]*\\)", "");
        value = value.replaceAll("(?i)benchmark\\s*\\([^)]*\\)", "");
        value = value.replaceAll("(?i)load_file\\s*\\([^)]*\\)", "");

        return value;
    }

    /**
     * 获取检测到的危险模式描述
     *
     * @param value 待检测内容
     * @return 危险模式描述，未检测到返回 null
     */
    public static String getDetectedPattern(String value) {
        if (StringUtils.isBlank(value)) return null;

        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return "危险模式: " + pattern.pattern();
            }
        }

        // 移除安全标识符后再检测
        String cleanedValue = removeSafeIdentifiers(value);

        // 检测关键词组合
        int keywordCount = 0;
        Set<String> foundKeywords = new HashSet<>();
        for (String keyword : SQL_KEYWORDS) {
            if (Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(cleanedValue).find()) {
                keywordCount++;
                foundKeywords.add(keyword);
            }
        }

        if (keywordCount >= 2 && hasDangerousSyntax(cleanedValue)) {
            return "多关键词组合: " + foundKeywords + " + 危险语法";
        }

        if (keywordCount >= 2 && hasDangerousKeywordPair(foundKeywords, cleanedValue)) {
            return "危险关键词配对: " + foundKeywords;
        }

        if (keywordCount == 1) {
            String keyword = foundKeywords.iterator().next();
            if (hasClearInjectionSyntax(cleanedValue, keyword)) {
                return "关键词 + 注入语法: " + keyword;
            }
        }

        return null;
    }
}
