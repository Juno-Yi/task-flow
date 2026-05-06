package com.junoyi.framework.wework.message;

import lombok.Setter;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.templatecard.TemplateCardButton;

import java.util.List;

/**
 * 按钮型模板卡片消息
 *
 * @author Fan
 */
@Setter
public class ButtonTemplateCardMessage extends AbstractWeWorkTemplateCardMessage {

    /**
     * 卡片类型，固定为"button_interaction"
     * 表示这是一个按钮交互型卡片
     */
    private String cardType = "button_interaction";

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
     * 任务ID
     * 用于标识和追踪卡片任务的唯一标识符
     */
    private String taskId;

    /**
     * 按钮列表
     * 卡片上显示的所有可点击按钮
     */
    private List<TemplateCardButton> buttons;

    /**
     * 来源描述信息
     * 显示在卡片左上角的来源标识
     */
    private String sourceDesc;

    /**
     * 构建企业微信按钮型模板卡片消息对象
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
                .taskId(taskId)
                .build();

        // 设置按钮列表和来源描述
        message.setButtons(buttons);
        message.setSourceDesc(sourceDesc);
        return message;
    }
}
