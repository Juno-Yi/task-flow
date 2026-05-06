package com.junoyi.framework.wework.model;

import lombok.Data;

/**
 * 企业微信通用接口响应
 *
 * @author Fan
 */
@Data
public class WeWorkScheduleApiResponse {

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}

