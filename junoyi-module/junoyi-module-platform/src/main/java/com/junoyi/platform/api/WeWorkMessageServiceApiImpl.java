package com.junoyi.platform.api;

import com.junoyi.platform.enums.PlatformType;
import org.springframework.stereotype.Service;

/**
 * 企业微信消息通知 API接口实现
 *
 * @author Fan
 */
@Service
public class WeWorkMessageServiceApiImpl implements PlatformMessageServiceApi {

    /**
     * 获取平台类型（企业微信）
     */
    @Override
    public PlatformType getPlatform() {
        return PlatformType.WEWORK;
    }

    /**
     * 发送文本消息
     *
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text 消息文本
     */
    @Override
    public void sendText(String weworkUserId, String text){

    }
}