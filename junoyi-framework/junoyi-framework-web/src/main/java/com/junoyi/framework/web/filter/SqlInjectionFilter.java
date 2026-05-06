package com.junoyi.framework.web.filter;

import com.junoyi.framework.web.properties.SQLInjectionProperties;
import com.junoyi.framework.web.sql.SqlInjectionHttpServletRequestWrapper;
import com.junoyi.framework.web.sql.SqlInjectionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * SQL 注入防护过滤器
 *
 * @author Fan
 */
public class SqlInjectionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SqlInjectionFilter.class);

    private final SQLInjectionProperties properties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public SqlInjectionFilter(SQLInjectionProperties properties) {
        this.properties = properties;
        // 设置自定义关键词
        if (properties.getCustomKeywords() != null && !properties.getCustomKeywords().isEmpty()) {
            SqlInjectionUtils.setCustomKeywords(new HashSet<>(properties.getCustomKeywords()));
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 判断是否需要过滤
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // DETECT 模式：先检测是否包含 SQL 注入
        if (properties.getMode() == SQLInjectionProperties.SQLInjectionMode.DETECT) {
            // 检测请求参数
            if (properties.isFilterParameter()) {
                String detected = checkParameters(request);
                if (detected != null) {
                    log.warn("[SQL注入拦截] 请求地址: {}, 参数包含 SQL 注入: {}", request.getRequestURI(), detected);
                    rejectRequest(response);
                    return;
                }
            }

            // 检测请求头
            if (properties.isFilterHeader()) {
                String detected = checkHeaders(request);
                if (detected != null) {
                    log.warn("[SQL注入拦截] 请求地址: {}, 请求头包含 SQL 注入: {}", request.getRequestURI(), detected);
                    rejectRequest(response);
                    return;
                }
            }

            // 检测请求体（需要包装请求）
            if (properties.isFilterBody() && hasRequestBody(request)) {
                // 读取请求体
                byte[] body = request.getInputStream().readAllBytes();
                if (body != null && body.length > 0) {
                    String bodyStr = new String(body, StandardCharsets.UTF_8);
                    if (SqlInjectionUtils.containsSqlInjection(bodyStr)) {
                        String pattern = SqlInjectionUtils.getDetectedPattern(bodyStr);
                        log.warn("[SQL注入拦截] 请求地址: {}, 请求体包含 SQL 注入, 触发规则: {}, 内容: {}", 
                                request.getRequestURI(), pattern, truncateForLog(bodyStr));
                        rejectRequest(response);
                        return;
                    }
                }
                // 创建包装器以便后续读取，保留原始 request 的 ContentType
                SqlInjectionHttpServletRequestWrapper wrappedRequest = new SqlInjectionHttpServletRequestWrapper(request, properties, body);
                filterChain.doFilter(wrappedRequest, response);
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        // CLEAN 模式：使用包装器清理
        SqlInjectionHttpServletRequestWrapper wrappedRequest = new SqlInjectionHttpServletRequestWrapper(request, properties);
        filterChain.doFilter(wrappedRequest, response);
    }

    /**
     * 判断是否跳过检测
     */
    private boolean shouldSkip(HttpServletRequest request) {
        if (!properties.isEnable()) return true;

        // 排除的请求方法
        String method = request.getMethod();
        if (properties.getExcludeMethods().stream().anyMatch(m -> m.equalsIgnoreCase(method))) {
            return true;
        }

        // GET/HEAD/OPTIONS 请求如果不检测参数则跳过
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
            return !properties.isFilterParameter();
        }

        // 排除的 URL
        String uri = request.getRequestURI();
        
        // 字典管理接口白名单 - 字典数据可能包含 SQL 关键词（如 update、delete 等）
        if (uri.contains("/system/dict/data") || uri.contains("/system/dict-data")) {
            return true;
        }
        
        // 权限池接口白名单 - 权限标识可能包含 SQL 关键词（如 system.api.user.delete）
        if (uri.contains("/system/permission-pool") || uri.contains("/system/permission/pool")) {
            return true;
        }
        
        // 权限管理接口白名单 - 权限标识可能包含 SQL 关键词
        if (uri.contains("/system/permission") && !uri.contains("/system/permission-pool")) {
            return true;
        }
        
        return properties.getExcludeUrls().stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /**
     * 判断请求是否有请求体
     */
    private boolean hasRequestBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) return false;
        // 文件上传跳过
        if (contentType.toLowerCase().contains("multipart/form-data")) return false;
        return request.getContentLength() > 0 || request.getContentLengthLong() > 0;
    }

    /**
     * 检测请求参数
     */
    private String checkParameters(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            // 跳过排除的参数
            if (isExcludedParam(name)) continue;
            
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (String value : values) {
                    if (SqlInjectionUtils.containsSqlInjection(value)) {
                        return name + "=" + value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断参数是否在排除列表中
     */
    private boolean isExcludedParam(String paramName) {
        if (properties.getExcludeParams() == null || properties.getExcludeParams().isEmpty()) {
            return false;
        }
        return properties.getExcludeParams().stream()
                .anyMatch(p -> p.equalsIgnoreCase(paramName));
    }

    /**
     * 检测请求头
     */
    private String checkHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (isStandardHeader(name)) continue;

            String value = request.getHeader(name);
            if (SqlInjectionUtils.containsSqlInjection(value)) {
                return name + ": " + value;
            }
        }
        return null;
    }

    /**
     * 拒绝请求
     */
    private void rejectRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":400,\"msg\":\"请求包含非法字符\"}");
    }

    /**
     * 判断是否为标准请求头
     */
    private boolean isStandardHeader(String name) {
        String lowerName = name.toLowerCase();
        return lowerName.equals("host") || lowerName.equals("connection") ||
                lowerName.equals("accept") || lowerName.equals("accept-language") ||
                lowerName.equals("accept-encoding") || lowerName.equals("content-type") ||
                lowerName.equals("content-length") || lowerName.equals("user-agent") ||
                lowerName.equals("authorization") || lowerName.equals("cookie") ||
                lowerName.equals("origin") || lowerName.equals("referer") ||
                lowerName.equals("cache-control") || lowerName.equals("pragma");
    }

    /**
     * 截断日志内容，避免日志过长
     */
    private String truncateForLog(String content) {
        if (content == null) return null;
        int maxLength = 500;
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...(truncated)";
    }
}
