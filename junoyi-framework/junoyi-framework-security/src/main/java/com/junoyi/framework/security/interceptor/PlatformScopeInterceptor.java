package com.junoyi.framework.security.interceptor;

import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.context.SecurityContext;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * 平台作用域拦截器
 * 用于限制接口只能被特定平台访问
 *
 * @author Fan
 */
@Component
public class PlatformScopeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 获取方法上的 @PlatformScope 注解
        PlatformScope platformScope = handlerMethod.getMethodAnnotation(PlatformScope.class);
        if (platformScope == null)
            return true;

        PlatformType[] allowedPlatforms = platformScope.value();
        if (allowedPlatforms.length == 0)
            return true;

        // 获取当前登录用户
        LoginUser loginUser = SecurityContext.get();
        if (loginUser == null)
            // 未登录，放行（由认证过滤器处理）
            return true;

        // 检查当前平台是否在允许列表中
        PlatformType currentPlatform = loginUser.getPlatformType();
        if (currentPlatform == null) {
            rejectRequest(response, "无法识别当前平台");
            return false;
        }

        boolean allowed = Arrays.asList(allowedPlatforms).contains(currentPlatform);
        if (!allowed) {
            rejectRequest(response, "当前平台无权访问此接口");
            return false;
        }

        return true;
    }

    /**
     * 拒绝请求
     */
    private void rejectRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"" + message + "\"}");
    }
}
