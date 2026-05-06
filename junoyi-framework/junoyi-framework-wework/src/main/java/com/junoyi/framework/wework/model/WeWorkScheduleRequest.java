package com.junoyi.framework.wework.model;

import lombok.Data;

import java.util.List;

/**
 * 企业微信日程请求模型
 *
 * @author Fan
 */
@Data
public class WeWorkScheduleRequest {

    /**
     * 日程标题
     */
    private String summary;

    /**
     * 日程描述
     */
    private String description;

    /**
     * 日程开始时间戳（秒）
     */
    private Long startTime;

    /**
     * 日程结束时间戳（秒）
     */
    private Long endTime;

    /**
     * 创建者企业微信用户ID
     */
    private String organizer;

    /**
     * 参与人企业微信用户ID列表
     */
    private List<String> attendees;

    /**
     * 备注/业务扩展字段
     */
    private String remarks;
}

