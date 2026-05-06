package com.junoyi.framework.wework.helper;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.wework.core.WeWorkClient;
import com.junoyi.framework.wework.exception.WeWorkException;
import com.junoyi.framework.wework.message.AbstractWeWorkTemplateCardMessage;
import com.junoyi.framework.wework.message.MarkdownMessage;
import com.junoyi.framework.wework.message.TextMessage;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 企业微信消息发送器工具实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class WeWorkAppMessageHelperImpl implements WeWorkAppMessageHelper {

    private static final Logger log = LoggerFactory.getLogger(WeWorkAppMessageHelperImpl.class);

    private final WeWorkClient weWorkClient;

    /**
     * 发送普通文本消息
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text 消息文本
     */
    @Override
    public void sendText(String weworkUserId, String text) {
        TextMessage message = new TextMessage();
        message.setTouser(weworkUserId);
        message.setAgentid(String.valueOf(weWorkClient.getProperties().getAgentId()));

        TextMessage.Content content = new TextMessage.Content();
        content.setContent(text);
        message.setText(content);

        send(message);
    }

    /**
     * 发送 Markdown 消息
     *
     * @param weworkUserId 消息接收用户企微唯一ID
     * @param text Markdown 内容
     */
    @Override
    public void sendMarkdown(String weworkUserId, String text) {
        MarkdownMessage message = new MarkdownMessage();
        message.setTouser(weworkUserId);
        message.setAgentid(String.valueOf(weWorkClient.getProperties().getAgentId()));

        MarkdownMessage.Content content = new MarkdownMessage.Content();
        content.setContent(text);
        message.setText(content);

        send(message);
    }

    /**
     * 发送模板卡片消息
     *
     * @param cardMessage 模板卡片消息
     */
    @Override
    public void sendCard(AbstractWeWorkTemplateCardMessage cardMessage) {
        send(cardMessage);
    }

    /**
     * 发送
     * @param message 消息
     */
    private void send(Object message) {
        WxCpMessage wxCpMessage = buildMessage(message);
        String accessToken = weWorkClient.getAccessToken();
        log.debug("企业微信消息发送准备完成，agentId={}, accessToken长度={}",
                weWorkClient.getProperties().getAgentId(), accessToken.length());

        try {
            weWorkClient.getMessageService().send(wxCpMessage);
        } catch (WxErrorException ex) {
            log.warn("企业微信消息发送失败，准备强制刷新 token 后重试", ex);
            retrySendAfterRefresh(wxCpMessage);
        }
    }

    /**
     * 尝试重新发送，在重新刷新AccessToken之后
     * @param message 消息
     */
    private void retrySendAfterRefresh(WxCpMessage message) {
        try {
            weWorkClient.getWxCpService().getAccessToken(true);
            weWorkClient.getWxCpService().getMessageService().send(message);
        } catch (WxErrorException retryEx) {
            throw buildSendException(retryEx);
        }
    }

    private WeWorkException buildSendException(WxErrorException ex) {
        Integer errorCode = ex.getError() == null ? null : ex.getError().getErrorCode();
        String errorMsg = ex.getError() == null ? ex.getMessage() : ex.getError().getErrorMsg();

        if (errorCode != null && errorCode == 60020) {
            return new WeWorkException(HttpStatus.FORBIDDEN,
                    "企业微信消息发送失败：当前服务访问IP不在企业微信白名单中，请将服务器出口IP加入企业微信应用白名单",
                    "MESSAGE_IP_WHITE_LIST");
        }

        return new WeWorkException(HttpStatus.BAD_REQUEST,
                "企业微信消息发送失败：" + (errorMsg == null ? "未知错误" : errorMsg),
                "MESSAGE_SEND");
    }


    /**
     * 构建发送的消息
     * @param message 消息
     * @return 构建好的消息
     */
    private WxCpMessage buildMessage(Object message) {
        if (message instanceof WxCpMessage wxCpMessage) {
            return wxCpMessage;
        }
        if (message instanceof TextMessage textMessage) {
            return WxCpMessage.TEXT()
                    .agentId(parseAgentId(textMessage.getAgentid()))
                    .toUser(textMessage.getTouser())
                    .content(textMessage.getText() == null ? null : textMessage.getText().getContent())
                    .build();
        }
        if (message instanceof MarkdownMessage markdownMessage) {
            return WxCpMessage.MARKDOWN()
                    .agentId(parseAgentId(markdownMessage.getAgentid()))
                    .toUser(markdownMessage.getTouser())
                    .content(markdownMessage.getText() == null ? null : markdownMessage.getText().getContent())
                    .build();
        }
        if (message instanceof AbstractWeWorkTemplateCardMessage cardMessage) {
            return cardMessage.build(weWorkClient.getProperties().getAgentId());
        }
        throw new WeWorkException(HttpStatus.BAD_REQUEST,
                "不支持的企业微信消息类型: " + message.getClass().getName(),
                "MESSAGE_TYPE");
    }

    /**
     * 解析应用ID
     * @param agentId 应用ID
     * @return 解析好的应用ID
     */
    private Integer parseAgentId(String agentId) {
        if (agentId == null || agentId.isBlank()) {
            return weWorkClient.getProperties().getAgentId();
        }
        return Integer.parseInt(agentId);
    }
}