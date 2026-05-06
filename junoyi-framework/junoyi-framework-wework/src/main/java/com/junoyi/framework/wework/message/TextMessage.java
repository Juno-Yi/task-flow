package com.junoyi.framework.wework.message;

import lombok.Data;

/**
 * 文本消息
 *
 * @author Fan
 */
@Data
public class TextMessage {

    /**
     * 接收消息的用户ID列表
     * 多个用户时使用竖线分隔，如："user1|user2"
     * 指定为"@all"时发送给所有人
     */
    private String touser;

    /**
     * 消息类型，固定为"text"
     * 用于标识此消息为文本类型
     */
    private String msgtype = "text";

    /**
     * 企业应用的agentid
     * 用于指定消息发送的目标应用
     */
    private String agentid;

    /**
     * 文本消息内容对象
     * 包含实际要发送的文本内容
     */
    private Content text;

    /**
     * 文本消息内容封装类
     * 用于包装具体的文本字符串
     */
    @Data
    public static class Content {
        /**
         * 文本消息的具体内容
         * 支持文本格式，长度有一定限制
         */
        private String content;
    }
}
