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
     * 发送平台Message
     */
    void sendMessage();
}
