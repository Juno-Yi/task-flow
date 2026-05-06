package com.junoyi.system.controller;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.service.ISysCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class SysCaptchaController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysCaptchaController.class);

    private final ISysCaptchaService captchaService;

    @Autowired(required = false)
    private CaptchaHelper captchaHelper;

    @Autowired(required = false)
    private CaptchaProperties captchaProperties;

    /**
     * 获取验证码配置信息
     * 前端通过此接口判断是否需要显示验证码
     *
     * @return R<Map<String, Object>> 验证码配置信息
     */
    @GetMapping("/config")
    public R<java.util.Map<String, Object>> getCaptchaConfig() {
        boolean enabled = captchaHelper != null && captchaProperties != null && captchaProperties.isEnable();
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", enabled);
        return R.ok(config);
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/image")
    public R<CaptchaResult> getImageCaptcha() {
        // 检查验证码是否启用
        boolean enabled = captchaHelper != null && captchaProperties != null && captchaProperties.isEnable();
        if (!enabled) {
            return R.fail("验证码功能已关闭");
        }
        return R.ok(captchaService.getImageCaptcha());
    }

    /**
     * 校验图片验证码
     */
    @PostMapping("/validate")
    public R<Boolean> validate(@RequestParam String captchaId, @RequestParam String code) {
        // 检查验证码是否启用
        boolean enabled = captchaHelper != null && captchaProperties != null && captchaProperties.isEnable();
        if (!enabled) {
            return R.ok(true); 
        }
        return R.ok(captchaService.validate(captchaId, code));
    }

}
