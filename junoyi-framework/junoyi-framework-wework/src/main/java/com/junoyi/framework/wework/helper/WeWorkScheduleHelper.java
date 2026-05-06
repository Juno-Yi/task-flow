package com.junoyi.framework.wework.helper;

import com.junoyi.framework.wework.model.WeWorkScheduleDetail;
import com.junoyi.framework.wework.model.WeWorkScheduleRequest;

/**
 * 企业微信日程工具接口
 *
 * @author Fan
 */
public interface WeWorkScheduleHelper {

    /**
     * 创建日程
     *
     * @param request 日程请求参数
     * @return 企业微信日程ID
     */
    String createSchedule(WeWorkScheduleRequest request);

    /**
     * 更新日程
     *
     * @param scheduleId 日程ID
     * @param request 日程请求参数
     */
    void updateSchedule(String scheduleId, WeWorkScheduleRequest request);

    /**
     * 查询日程详情
     *
     * @param scheduleId 日程ID
     * @return 日程详情
     */
    WeWorkScheduleDetail getSchedule(String scheduleId);

    /**
     * 删除日程
     *
     * @param scheduleId 日程ID
     */
    void deleteSchedule(String scheduleId);
}

