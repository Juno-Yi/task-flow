package com.junoyi.framework.web.config;

import com.junoyi.framework.web.interceptor.AccessLogInterceptor;
import com.junoyi.framework.web.properties.AccessLogProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类，用于自定义Spring MVC的配置
 *
 * <p>该类实现了WebMvcConfigurer接口，可以重写其中的方法来自定义Spring MVC的各种配置，
 * 如拦截器、视图解析器、静态资源处理等。</p>
 *
 * @author Fan
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(AccessLogProperties.class)
public class WebMvcConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebMvcConfiguration.class);

    private final AccessLogProperties accessLogProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册访问日志拦截器
        if (accessLogProperties.isEnable()) {
            log.info("[Access Log] Access log interceptor enabled, slow threshold: {}ms",
                    accessLogProperties.getSlowRequestThreshold());
            registry.addInterceptor(new AccessLogInterceptor(accessLogProperties))
                    .addPathPatterns("/**")
                    .order(Integer.MIN_VALUE); // 最先执行
        }
    }
}
