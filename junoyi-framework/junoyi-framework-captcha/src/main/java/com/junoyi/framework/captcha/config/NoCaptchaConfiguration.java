package com.junoyi.framework.captcha.config;

import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.framework.captcha.helper.NoCaptchaHelper;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码禁用时的配置
 * 当验证码功能关闭时，提供空实现以避免依赖注入失败
 *
 * @author Fan
 */
@Configuration
@ConditionalOnProperty(prefix = "junoyi.captcha", name = "enable", havingValue = "false")
public class NoCaptchaConfiguration {

    private static final JunoYiLog log = JunoYiLogFactory.getLogger(NoCaptchaConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(CaptchaHelper.class)
    public CaptchaHelper noCaptchaHelper() {
        log.info("[Captcha] Captcha is disabled, registering NoCaptchaHelper");
        return new NoCaptchaHelper();
    }
}

