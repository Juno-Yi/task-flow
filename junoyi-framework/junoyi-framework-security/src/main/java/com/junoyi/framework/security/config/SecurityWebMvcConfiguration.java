package com.junoyi.framework.security.config;

import com.junoyi.framework.security.interceptor.PlatformScopeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Web MVC 配置
 * 用于注册拦截器
 *
 * @author Fan
 */
@AutoConfiguration
@RequiredArgsConstructor
public class SecurityWebMvcConfiguration implements WebMvcConfigurer {

    private final PlatformScopeInterceptor platformScopeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册平台作用域拦截器
        registry.addInterceptor(platformScopeInterceptor)
                .addPathPatterns("/**")
                .order(0);
    }
}
