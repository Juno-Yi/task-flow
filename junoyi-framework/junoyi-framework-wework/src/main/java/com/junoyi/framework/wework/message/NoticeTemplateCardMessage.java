package com.junoyi.framework.wework.message;

import lombok.Setter;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;

/**
 * 通知型模板卡片消息
 *
 * @author Fan
 */
@Setter
public class NoticeTemplateCardMessage extends AbstractWeWorkTemplateCardMessage {

    /**
     * 卡片类型，固定为"text_notice"
     * 表示这是一个通知型文本卡片
     */
    private String cardType = "text_notice";

    /**
     * 来源描述信息
     * 显示在卡片左上角的来源标识
     */
    private String sourceDesc;

    /**
     * 主标题
     * 卡片的主要标题内容
     */
    private String mainTitle;

    /**
     * 主标题描述
     * 主标题下方的辅助说明文字
     */
    private String mainDesc;

    /**
     * 副标题
     * 位于主标题下方的次要标题
     */
    private String subTitle;

    /**
     * 强调内容的标题
     * 以突出样式显示的重要信息标题
     */
    private String emphasisTitle;

    /**
     * 强调内容的描述
     * 以突出样式显示的重要信息内容
     */
    private String emphasisDesc;

    /**
     * 跳转链接URL
     * 用户点击卡片后跳转的目标地址
     */
    private String jumpUrl;

    /**
     * 跳转按钮的标题文本
     * 默认为"查看详情"
     */
    private String jumpTitle = "查看详情";

    /**
     * 引用区域的文本内容
     * 用于显示引用的消息或上下文
     */
    private String quoteText;

    /**
     * 引用区域的跳转链接
     * 点击引用区域时跳转的URL
     */
    private String quoteUrl;

    /**
     * 引用区域的标题
     * 显示在引用区域的标题文字
     */
    private String quoteTitle;

    /**
     * 构建企业微信模板卡片消息对象
     *
     * @param defaultAgentId 默认的应用ID，当未指定agentId时使用此值
     * @return 构建完成的WxCpMessage对象
     */
    @Override
    public WxCpMessage build(Integer defaultAgentId) {
        // 构建基础模板卡片消息结构
        WxCpMessage message = WxCpMessage.TEMPLATECARD()
                .agentId(resolveAgentId(defaultAgentId))
                .toUser(getTouser())
                .cardType(cardType)
                .mainTitleTitle(mainTitle)
                .mainTitleDesc(mainDesc)
                .subTitleText(subTitle)
                .emphasisContentTitle(emphasisTitle)
                .emphasisContentDesc(emphasisDesc)
                .build();

        // 设置卡片动作配置和引用区域
        message.setCardActionType(1);
        message.setCardActionUrl(jumpUrl);
        message.setSourceDesc(sourceDesc);
        message.setQuoteArea(buildQuoteArea());
        message.setJumps(java.util.List.of(
                me.chanjar.weixin.cp.bean.templatecard.TemplateCardJump.builder()
                        .type(1)
                        .url(jumpUrl)
                        .title(jumpTitle)
                        .build()
        ));
        return message;
    }

    /**
     * 构建引用区域对象
     * 当quoteText为空时返回null，不显示引用区域
     *
     * @return QuoteArea对象，如果quoteText为空则返回null
     */
    private me.chanjar.weixin.cp.bean.templatecard.QuoteArea buildQuoteArea() {
        if (quoteText == null || quoteText.isBlank()) {
            return null;
        }
        return me.chanjar.weixin.cp.bean.templatecard.QuoteArea.builder()
                .type(1)
                .url(quoteUrl)
                .title(quoteTitle)
                .quoteText(quoteText)
                .build();
    }

}
