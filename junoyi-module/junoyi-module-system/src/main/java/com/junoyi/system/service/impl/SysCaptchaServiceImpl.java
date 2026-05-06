package com.junoyi.system.service.impl;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.system.service.ISysCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 验证码服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysCaptchaServiceImpl implements ISysCaptchaService {

    private final CaptchaHelper captchaHelper;

    /**
     * 获取图片验证码
     *
     * @return 验证码结果对象，包含验证码ID、图片数据等信息
     */
    @Override
    public CaptchaResult getImageCaptcha() {
        return captchaHelper.generate(CaptchaType.IMAGE);
    }


    /**
     * 验证验证码
     *
     * @param captchaId 验证码标识ID
     * @param code 用户输入的验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    @Override
    public boolean validate(String captchaId, String code) {
        return captchaHelper.validate(captchaId, code);
    }

}
