package com.junoyi.framework.wework.helper;

import java.util.List;

/**
 * 企业微信群机器人消息发送工具
 *
 * @author Fan
 */
public interface WeWorkGroupBotHelper {

    /**
     * 发送文本消息到全部已配置 webhook
     */
    void sendText(String content);

    /**
     * 发送带 @ 的文本消息到全部已配置 webhook
     */
    void sendText(String content, List<String> mentionedList, List<String> mentionedMobileList);

    /**
     * 发送 Markdown 消息到全部已配置 webhook
     */
    void sendMarkdown(String content);

    /**
     * 发送文本消息到指定 webhook
     */
    void sendText(String webhook, String content);

    /**
     * 发送带 @ 的文本消息到指定 webhook
     */
    void sendText(String webhook, String content, List<String> mentionedList, List<String> mentionedMobileList);

    /**
     * 发送 Markdown 消息到指定 webhook
     */
    void sendMarkdown(String webhook, String content);
}

