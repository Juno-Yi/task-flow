package com.junoyi.framework.wework.message;

import lombok.Data;

/**
 * 企业微信群机器人 Markdown 消息
 *
 * @author Fan
 */
@Data
public class WeWorkGroupBotMarkdownMessage {

    private final String msgtype = "markdown";
    private Markdown markdown;

    @Data
    public static class Markdown {
        private String content;
    }
}

