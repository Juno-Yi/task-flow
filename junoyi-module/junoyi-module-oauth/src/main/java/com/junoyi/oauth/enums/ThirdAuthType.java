package com.junoyi.oauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方登录类型枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum ThirdAuthType {

    /**
     * 企业微信
     */
    WEWORK("wework", "企业微信"),

    /**
     * 微信
     */
    WECHAT("wechat", "微信"),

    /**
     * 钉钉
     */
    DINGTALK("dingtalk", "钉钉"),

    /**
     * 飞书
     */
    FEISHU("feishu", "飞书");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;
}
