package com.junoyi.oauth.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Oauth配置 PO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oauth_config")
public class OauthConfig extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 平台（github、gitee、wework、feishu、dingtalk、wechat）
     */
    public String platform;

    /**
     * 状态( 1启用， 2禁用）
     */
    private Integer status;

    /**
     * 配置 Key
     */
    private String configKey;

    /**
     * 配置 Value
     */
    private String configValue;

    /**
     * 回调URL地址
     */
    public String redirectUri;

    /**
     * 是否系统内置
     */
    private Boolean isSystem;
}