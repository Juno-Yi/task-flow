package com.junoyi.oauth.domain.dto;

import com.junoyi.framework.core.domain.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth配置查询DTO
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OauthConfigQueryDTO extends PageQuery {

    /**
     * 平台
     */
    private String platform;

    /**
     * 状态（0禁用，1启用）
     */
    private Integer status;

}

