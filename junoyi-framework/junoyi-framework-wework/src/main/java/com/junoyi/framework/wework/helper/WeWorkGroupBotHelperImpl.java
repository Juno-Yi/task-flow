package com.junoyi.framework.wework.helper;

import com.junoyi.framework.wework.exception.WeWorkException;
import com.junoyi.framework.wework.message.WeWorkGroupBotMarkdownMessage;
import com.junoyi.framework.wework.message.WeWorkGroupBotTextMessage;
import com.junoyi.framework.wework.properties.WeWorkProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 企业微信群机器人消息发送工具实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class WeWorkGroupBotHelperImpl implements WeWorkGroupBotHelper {

    private final RestTemplate restTemplate;
    private final WeWorkProperties weWorkProperties;

    /**
     * 发送文本消息到全部已配置 webhook
     */
    @Override
    public void sendText(String content) {
        batchSend(weWorkProperties.getGroupBot().getWebhooks(), buildTextMessage(content, null, null));
    }

    @Override
    public void sendText(String content, List<String> mentionedList, List<String> mentionedMobileList) {
        batchSend(weWorkProperties.getGroupBot().getWebhooks(), buildTextMessage(content, mentionedList, mentionedMobileList));
    }

    /**
     * 发送 Markdown 消息到全部已配置 webhook
     */
    @Override
    public void sendMarkdown(String content) {
        batchSend(weWorkProperties.getGroupBot().getWebhooks(), buildMarkdownMessage(content));
    }

    /**
     * 发送文本消息到指定 webhook
     */
    @Override
    public void sendText(String webhook, String content) {
        doSend(webhook, buildTextMessage(content, null, null));
    }

    @Override
    public void sendText(String webhook, String content, List<String> mentionedList, List<String> mentionedMobileList) {
        doSend(webhook, buildTextMessage(content, mentionedList, mentionedMobileList));
    }

    /**
     * 发送 Markdown 消息到指定 webhook
     */
    @Override
    public void sendMarkdown(String webhook, String content) {
        doSend(webhook, buildMarkdownMessage(content));
    }

    private void batchSend(List<String> webhooks, Object body) {
        if (CollectionUtils.isEmpty(webhooks)) {
            return;
        }
        webhooks.stream().filter(StringUtils::hasText).distinct().forEach(webhook -> doSend(webhook, body));
    }

    private void doSend(String webhook, Object body) {
        if (!StringUtils.hasText(webhook)) {
            throw new IllegalArgumentException("企业微信群机器人 webhook 未配置");
        }
        ResponseEntity<Object> response = restTemplate.postForEntity(webhook, body, Object.class);
        Map<?, ?> result = response.getBody() instanceof Map<?, ?> bodyMap ? bodyMap : null;
        Object errcode = result == null ? null : result.get("errcode");
        if (errcode instanceof Number number && number.intValue() == 0) {
            return;
        }
        String errmsg = result == null ? "未知错误" : String.valueOf(result.get("errmsg"));
        throw new WeWorkException(400, "企业微信群机器人消息发送失败：" + errmsg, "GROUP_BOT_SEND");
    }

    private List<String> normalizeMentionList(List<String> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        List<String> result = source.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
        return result.isEmpty() ? null : result;
    }

    private WeWorkGroupBotTextMessage buildTextMessage(String content, List<String> mentionedList, List<String> mentionedMobileList) {
        WeWorkGroupBotTextMessage message = new WeWorkGroupBotTextMessage();
        WeWorkGroupBotTextMessage.Text text = new WeWorkGroupBotTextMessage.Text();
        text.setContent(content);
        text.setMentioned_list(normalizeMentionList(mentionedList));
        text.setMentioned_mobile_list(normalizeMentionList(mentionedMobileList));
        message.setText(text);
        return message;
    }

    private WeWorkGroupBotMarkdownMessage buildMarkdownMessage(String content) {
        WeWorkGroupBotMarkdownMessage message = new WeWorkGroupBotMarkdownMessage();
        WeWorkGroupBotMarkdownMessage.Markdown markdown = new WeWorkGroupBotMarkdownMessage.Markdown();
        markdown.setContent(content);
        message.setMarkdown(markdown);
        return message;
    }
}

