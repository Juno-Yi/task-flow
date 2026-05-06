package com.junoyi.framework.wework.model;

import lombok.Data;

/**
 * 企业微信创建日程响应
 *
 * @author Fan
 */
@Data
public class WeWorkScheduleCreateResponse {

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 日程ID
     */
    private String schedule_id;
}

