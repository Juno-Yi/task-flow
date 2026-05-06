package com.junoyi.framework.captcha.helper;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;

/**
 * 验证码帮助类接口 - 业务层统一调用入口
 *
 * @author Fan
 */
public interface CaptchaHelper {
    /**
     * 生成默认类型验证码
     *
     * @return 验证码结果
     */
    CaptchaResult generate();

    /**
     * 生成指定类型验证码
     *
     * @param type 验证码类型
     * @return 验证码结果
     */
    CaptchaResult generate(CaptchaType type);

    /**
     * 校验图片验证码
     *
     * @param captchaId 验证码ID
     * @param code      用户输入的验证码
     * @return 是否校验通过
     */
    boolean validate(String captchaId, String code);

}
