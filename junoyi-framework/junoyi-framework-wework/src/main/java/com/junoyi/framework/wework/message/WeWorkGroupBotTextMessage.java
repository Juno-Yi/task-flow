package com.junoyi.framework.wework.message;

import lombok.Data;

import java.util.List;

/**
 * 企业微信群机器人文本消息
 *
 * @author Fan
 */
@Data
public class WeWorkGroupBotTextMessage {

    private final String msgtype = "text";
    private Text text;

    @Data
    public static class Text {
        private String content;
        private List<String> mentioned_list;
        private List<String> mentioned_mobile_list;
    }
}

