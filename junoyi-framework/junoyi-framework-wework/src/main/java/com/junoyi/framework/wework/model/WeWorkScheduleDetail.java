package com.junoyi.framework.wework.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 企业微信日程详情
 *
 * @author Fan
 */
@Data
public class WeWorkScheduleDetail {

    /**
     * 企业微信日程ID
     */
    private String scheduleId;

    /**
     * 原始返回数据
     */
    private Map<String, Object> raw;

    /**
     * 日程标题
     */
    private String summary;

    /**
     * 日程描述
     */
    private String description;

    /**
     * 开始时间戳（秒）
     */
    private Long startTime;

    /**
     * 结束时间戳（秒）
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
}

