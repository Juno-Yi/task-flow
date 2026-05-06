package com.junoyi.framework.wework.helper;

import com.junoyi.framework.wework.message.AbstractWeWorkTemplateCardMessage;

/**
 * 企业微信应用消息发送者工具接口
 *
 * @author Fan
 */
public interface WeWorkAppMessageHelper {

    /**
     * 发送文本消息
     *
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text 消息文本
     */
    void sendText(String weworkUserId, String text);

    /**
     * 发送 Markdown 消息
     *
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text Markdown 内容
     */
    void sendMarkdown(String weworkUserId, String text);

    /**
     * 发送模板卡片消息
     *
     * @param cardMessage 模板卡片消息
     */
    void sendCard(AbstractWeWorkTemplateCardMessage cardMessage);
}