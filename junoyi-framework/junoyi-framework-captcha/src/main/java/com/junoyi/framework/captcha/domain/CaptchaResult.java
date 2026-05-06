package com.junoyi.framework.captcha.domain;

import com.junoyi.framework.captcha.enums.CaptchaType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 验证码生成结果
 *
 * @author Fan
 */
@Data
@Accessors(chain = true)
public class CaptchaResult implements Serializable {
    /**
     * 验证码唯一标识 (用于后续验证)
     */
    private String captchaId;
    /**
     * 验证码类型
     */
    private CaptchaType type;
    /**
     * 图片验证码 - Base64编码的图片
     */
    private String image;
    /**
     * 滑块验证码 - 背景图片 Base64
     */
    private String backgroundImage;
    /**
     * 滑块验证码 - 滑块图片 Base64
     */
    private String sliderImage;
    /**
     * 滑块验证码 - 背景图宽度
     */
    private Integer backgroundWidth;
    /**
     * 滑块验证码 - 背景图高度
     */
    private Integer backgroundHeight;
    /**
     * 点选验证码 - 原始图片 Base64
     */
    private String originalImage;
    /**
     * 点选验证码 - 需要点击的文字提示
     */
    private String wordList;
    /**
     * 验证码过期时间 (秒)
     */
    private Integer expireSeconds;
}
