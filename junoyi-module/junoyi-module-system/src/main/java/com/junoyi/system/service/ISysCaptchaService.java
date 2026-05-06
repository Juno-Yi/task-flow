package com.junoyi.system.service;

import com.junoyi.framework.captcha.domain.CaptchaResult;

/**
 * 验证码服务接口
 *
 * @author Fan
 */
public interface ISysCaptchaService {

    /**
     * 获取图形验证码
     */
    CaptchaResult getImageCaptcha();

    /**
     * 校验图片验证码
     */
    boolean validate(String captchaId, String code);

}
