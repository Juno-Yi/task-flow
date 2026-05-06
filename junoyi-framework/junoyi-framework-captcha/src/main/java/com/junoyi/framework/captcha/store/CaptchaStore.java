package com.junoyi.framework.captcha.store;

/**
 * 验证码存储接口
 *
 * @author Fan
 */
public interface CaptchaStore {
    /**
     * 存储验证码
     *
     * @param captchaId     验证码ID
     * @param value         验证码值
     * @param expireSeconds 过期时间(秒)
     */
    void save(String captchaId, String value, int expireSeconds);

    /**
     * 获取验证码
     *
     * @param captchaId 验证码ID
     * @return 验证码值, 不存在返回null
     */
    String get(String captchaId);

    /**
     * 删除验证码
     *
     * @param captchaId 验证码ID
     */
    void remove(String captchaId);

    /**
     * 验证并删除验证码 (一次性验证)
     *
     * @param captchaId 验证码ID
     * @param value     用户输入的值
     * @return 是否验证通过
     */
    boolean validateAndRemove(String captchaId, String value);
}
