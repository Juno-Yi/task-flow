package com.junoyi.platform.api;

import com.junoyi.platform.enums.PlatformType;

/**
 * 平台消息通知 API 接口
 *
 * @author Fan
 */
public interface PlatformMessageServiceApi {
    /**
     * 获取平台类型
     * @return 平台类型
     */
    PlatformType getPlatform();

    /**
     * 发送文本消息
     *
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text 消息文本
     */
    void sendText(String weworkUserId, String text);
}
