package com.junoyi.framework.wework.message;

import me.chanjar.weixin.cp.bean.message.WxCpMessage;

/**
 * 企业微信模板卡片消息基类
 * <p>
 * 框架提供统一发送入口，业务侧可以直接使用内置模板，
 * 也可以继承本基类自定义卡片消息内容。
 * </p>
 *
 * @author Fan
 */
public abstract class AbstractWeWorkTemplateCardMessage {

    /**
     * 接收用户企微ID
     */
    private String touser;

    /**
     * 应用 agentId，为空时默认取框架配置
     */
    private String agentid;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    /**
     * 构建企业微信 SDK 消息对象
     *
     * @param defaultAgentId 默认应用ID
     * @return SDK 消息对象
     */
    public abstract WxCpMessage build(Integer defaultAgentId);

    protected Integer resolveAgentId(Integer defaultAgentId) {
        if (agentid == null || agentid.isBlank()) {
            return defaultAgentId;
        }
        return Integer.parseInt(agentid);
    }
}

