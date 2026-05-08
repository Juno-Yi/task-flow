package com.junoyi.oauth.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * Oauth配置 VO 数据对象
 *
 * @author Fan
 */
@Data
public class OauthConfigVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 平台
     */
    private String platform;

    /**
     * 平台标签（字典翻译）
     */
    private String platformLabel;

    /**
     * 状态
     */
    private Integer status;

    private String statusLabel;

    private String statusType;

    /**
     * 回调地址
     */
    public String redirectUrl;

    /**
     * 配置Key
     */
    private String configKey;

    /**
     * 配置Value
     */
    private String configValue;

    /**
     * 是否系统内置
     */
    private Boolean isSystem;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

}