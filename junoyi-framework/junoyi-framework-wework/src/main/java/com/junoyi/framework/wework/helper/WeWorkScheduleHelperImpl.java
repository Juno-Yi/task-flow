package com.junoyi.framework.wework.helper;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junoyi.framework.wework.core.WeWorkClient;
import com.junoyi.framework.wework.exception.WeWorkException;
import com.junoyi.framework.wework.model.WeWorkScheduleApiResponse;
import com.junoyi.framework.wework.model.WeWorkScheduleCreateResponse;
import com.junoyi.framework.wework.model.WeWorkScheduleDetail;
import com.junoyi.framework.wework.model.WeWorkScheduleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信日程工具实现
 *
 * @author Fan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeWorkScheduleHelperImpl implements WeWorkScheduleHelper {

    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/oa/schedule";

    private final WeWorkClient weWorkClient;
    private final ObjectMapper objectMapper;

    /**
     * 创建日程
     *
     * @param request 日程请求参数
     * @return 企业微信日程ID
     */
    @Override
    public String createSchedule(WeWorkScheduleRequest request) {
        validateRequest(request);
        String url = BASE_URL + "/add?access_token=" + getAccessToken();
        String body = toJson(buildAddOrUpdateBody(null, request));
        WeWorkScheduleCreateResponse response = post(url, body, WeWorkScheduleCreateResponse.class);
        validateResponse(response.getErrcode(), response.getErrmsg(), "创建日程失败");
        return response.getSchedule_id();
    }

    /**
     * 更新日程
     *
     * @param scheduleId 日程ID
     * @param request 日程请求参数
     */
    @Override
    public void updateSchedule(String scheduleId, WeWorkScheduleRequest request) {
        if (!StringUtils.hasText(scheduleId)) {
            throw new WeWorkException("企业微信日程ID不能为空");
        }
        validateRequest(request);
        String url = BASE_URL + "/update?access_token=" + getAccessToken();
        String body = toJson(buildAddOrUpdateBody(scheduleId, request));
        WeWorkScheduleApiResponse response = post(url, body, WeWorkScheduleApiResponse.class);
        validateResponse(response.getErrcode(), response.getErrmsg(), "更新日程失败");
    }

    /**
     * 查询日程详情
     *
     * @param scheduleId 日程ID
     * @return 日程详情
     */
    @Override
    public WeWorkScheduleDetail getSchedule(String scheduleId) {
        if (!StringUtils.hasText(scheduleId)) {
            throw new WeWorkException("企业微信日程ID不能为空");
        }
        String url = BASE_URL + "/get?access_token=" + getAccessToken();
        String body = toJson(Map.of("schedule_id_list", List.of(scheduleId)));
        Map<String, Object> response = post(url, body, new TypeReference<Map<String, Object>>() {});
        validateResponse((Integer) response.get("errcode"), (String) response.get("errmsg"), "查询日程失败");
        return parseDetail(response);
    }

    /**
     * 删除日程
     *
     * @param scheduleId 日程ID
     */
    @Override
    public void deleteSchedule(String scheduleId) {
        if (!StringUtils.hasText(scheduleId)) {
            throw new WeWorkException("企业微信日程ID不能为空");
        }
        String url = BASE_URL + "/del?access_token=" + getAccessToken();
        String body = toJson(Map.of("schedule_id", scheduleId));
        WeWorkScheduleApiResponse response = post(url, body, WeWorkScheduleApiResponse.class);
        validateResponse(response.getErrcode(), response.getErrmsg(), "删除日程失败");
    }

    private Map<String, Object> buildAddOrUpdateBody(String scheduleId, WeWorkScheduleRequest request) {
        Map<String, Object> calendar = new HashMap<>();
        if (StringUtils.hasText(scheduleId)) calendar.put("schedule_id", scheduleId);
        calendar.put("summary", request.getSummary());
        calendar.put("description", request.getDescription());
        calendar.put("start_time", request.getStartTime());
        calendar.put("end_time", request.getEndTime());
        calendar.put("admins", buildAdminUsers(request.getOrganizer()));
        calendar.put("attendees", buildUserObjects(request.getAttendees()));
        if (StringUtils.hasText(request.getRemarks())) calendar.put("remarks", request.getRemarks());
        return Map.of("schedule", calendar);
    }

    private void validateRequest(WeWorkScheduleRequest request) {
        if (request == null) throw new WeWorkException("日程参数不能为空");
        if (!StringUtils.hasText(request.getSummary())) throw new WeWorkException("日程标题不能为空");
        if (!StringUtils.hasText(request.getOrganizer())) throw new WeWorkException("创建人不能为空");
        if (request.getStartTime() == null || request.getEndTime() == null) throw new WeWorkException("日程时间不能为空");
        if (request.getEndTime() < request.getStartTime()) throw new WeWorkException("结束时间不能早于开始时间");
    }

    private String getAccessToken() {
        try {
            return weWorkClient.getAccessToken();
        } catch (Exception e) {
            log.error("获取企业微信 accessToken 失败", e);
            throw new WeWorkException("获取企业微信 accessToken 失败");
        }
    }


    private <T> T post(String url, String body, Class<T> clazz) {
        try (HttpResponse response = HttpRequest.post(url)
                .contentType(ContentType.JSON.getValue())
                .body(body)
                .execute()) {
            String responseBody = response.body();
            log.debug("WeWork schedule api response: {}", responseBody);
            return objectMapper.readValue(responseBody, clazz);
        } catch (Exception e) {
            log.error("调用企业微信日程接口失败", e);
            throw new WeWorkException("调用企业微信日程接口失败");
        }
    }

    private <T> T post(String url, String body, TypeReference<T> typeReference) {
        try (HttpResponse response = HttpRequest.post(url)
                .contentType(ContentType.JSON.getValue())
                .body(body)
                .execute()) {
            String responseBody = response.body();
            log.debug("WeWork schedule api response: {}", responseBody);
            return objectMapper.readValue(responseBody, typeReference);
        } catch (Exception e) {
            log.error("调用企业微信日程接口失败", e);
            throw new WeWorkException("调用企业微信日程接口失败");
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("企业微信日程参数序列化失败", e);
            throw new WeWorkException("企业微信日程参数序列化失败");
        }
    }

    private WeWorkScheduleDetail parseDetail(Map<String, Object> response) {
        Map<?, ?> scheduleMap = null;
        Object scheduleObj = response.get("schedule");
        if (scheduleObj instanceof Map<?, ?> map) {
            scheduleMap = map;
        }

        if (scheduleMap == null) {
            Object scheduleListObj = response.get("schedule_list");
            if (scheduleListObj instanceof List<?> scheduleList && !scheduleList.isEmpty()) {
                Object first = scheduleList.get(0);
                if (first instanceof Map<?, ?> firstMap) {
                    scheduleMap = firstMap;
                }
            }
        }

        if (scheduleMap == null) {
            WeWorkScheduleDetail detail = new WeWorkScheduleDetail();
            detail.setRaw(response);
            return detail;
        }

        WeWorkScheduleDetail detail = new WeWorkScheduleDetail();
        detail.setRaw(response);
        detail.setScheduleId((String) scheduleMap.get("schedule_id"));
        detail.setSummary((String) scheduleMap.get("summary"));
        detail.setDescription((String) scheduleMap.get("description"));
        detail.setOrganizer(extractOrganizer(scheduleMap.get("admins")));
        detail.setAttendees(extractAttendees(scheduleMap.get("attendees")));
        detail.setStartTime(toLong(scheduleMap.get("start_time")));
        detail.setEndTime(toLong(scheduleMap.get("end_time")));
        return detail;
    }

    private List<String> buildAdminUsers(String organizer) {
        if (!StringUtils.hasText(organizer)) {
            return List.of();
        }
        return List.of(organizer);
    }

    private List<Map<String, String>> buildUserObjects(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return userIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .map(userId -> Map.of("userid", userId))
                .toList();
    }

    private String extractOrganizer(Object adminsObj) {
        if (adminsObj instanceof List<?> admins && !admins.isEmpty()) {
            Object first = admins.get(0);
            if (first instanceof Map<?, ?> firstMap) {
                Object userId = firstMap.get("userid");
                return userId == null ? null : String.valueOf(userId);
            }
            return first == null ? null : String.valueOf(first);
        }
        return null;
    }

    private List<String> extractAttendees(Object attendeesObj) {
        if (attendeesObj instanceof List<?> attendees) {
            return attendees.stream().map(item -> {
                if (item instanceof Map<?, ?> itemMap) {
                    Object userId = itemMap.get("userid");
                    return userId == null ? null : String.valueOf(userId);
                }
                return item == null ? null : String.valueOf(item);
            }).filter(StringUtils::hasText).toList();
        }
        return List.of();
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private void validateResponse(Integer errcode, String errmsg, String message) {
        if (errcode != null && errcode != 0) {
            throw new WeWorkException(message + "，errcode=" + errcode + "，errmsg=" + errmsg);
        }
    }
}
