package com.junoyi.oauth.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Oauth 配置 PO 数据对象
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
     * 平台
     */
    private String platform;

    /**
     * 配置 key
     */
    private String configKey;

    /**
     * 配置 value
     */
    private String configValue;

}