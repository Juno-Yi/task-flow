package com.junoyi.framework.captcha.helper;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;

/**
 * 验证码禁用时的空实现
 * 当验证码功能关闭时，提供此实现以避免依赖注入失败
 *
 * @author Fan
 */
public class NoCaptchaHelper implements CaptchaHelper {

    private static final JunoYiLog log = JunoYiLogFactory.getLogger(NoCaptchaHelper.class);

    public NoCaptchaHelper() {
        log.warn("[Captcha] Captcha is disabled, using NoCaptchaHelper");
    }

    @Override
    public CaptchaResult generate() {
        log.warn("[Captcha] Attempted to generate captcha while captcha is disabled");
        // 返回空结果，不抛异常
        return null;
    }

    @Override
    public CaptchaResult generate(CaptchaType type) {
        log.warn("[Captcha] Attempted to generate captcha while captcha is disabled");
        // 返回空结果，不抛异常
        return null;
    }

    @Override
    public boolean validate(String captchaId, String code) {
        // 验证码关闭时，始终返回 true（跳过验证）
        return true;
    }
}

