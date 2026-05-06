package com.junoyi.framework.captcha.properties;

import com.junoyi.framework.captcha.enums.CaptchaType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.captcha")
public class CaptchaProperties {
    /**
     * 是否启用验证码
     */
    private boolean enable = true;
    /**
     * 默认验证码类型
     */
    private CaptchaType type = CaptchaType.IMAGE;
    /**
     * 验证码过期时间 (秒)
     */
    private int expireSeconds = 120;
    /**
     * 图片验证码配置
     */
    private ImageCaptcha image = new ImageCaptcha();


    @Data
    public static class ImageCaptcha {
        /**
         * 验证码长度
         */
        private int length = 4;
        /**
         * 图片宽度
         */
        private int width = 150;
        /**
         * 图片高度
         */
        private int height = 50;
        /**
         * 干扰线数量
         */
        private int lineCount = 4;
        /**
         * 干扰圆圈数量
         */
        private int circleCount = 15;
        /**
         * 扭曲程度 (0-1, 0=不扭曲, 1=最大扭曲)
         */
        private double distortion = 0.3;
        /**
         * 噪点数量
         */
        private int noiseCount = 50;
        /**
         * 字体大小
         */
        private int fontSize = 32;
        /**
         * 验证码类型: math(数学运算), char(字符)
         */
        private String codeType = "math";
    }


}
