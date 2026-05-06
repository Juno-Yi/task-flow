package com.junoyi.framework.captcha.generator;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;

/**
 * 验证码生成器接口
 *
 * @author Fan
 */
public interface CaptchaGenerator {
    /**
     * 获取支持的验证码类型
     */
    CaptchaType getType();

    /**
     * 生成验证码
     *
     * @return 验证码结果
     */
    CaptchaResult generate();

    /**
     * 校验验证码
     *
     * @param captchaId 验证码ID
     * @param params    校验参数 (不同类型验证码参数不同)
     * @return 是否校验通过
     */
    boolean validate(String captchaId, Object params);
}
