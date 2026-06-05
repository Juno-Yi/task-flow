package com.junoyi.platform.client;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.platform.properties.WeWorkProperties;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * 企业微信统一客户端
 *
 * 如果platform模块业务中如果需要调用关于企业微信应用的API，
 * 都需要统一去注入获取WeWorkClient的bean实例，然后通过本类实例操作调用。
 *
 * @author Fan
 */
public class WeWorkClient {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(WeWorkClient.class);

    private static final int MAX_RETRY_TIMES = 3;

    /**
     * 企业微信配置参数
     */
    private final WeWorkProperties properties;

    /**
     * 企业微信服务
     */
    private final WxCpService wxCpService;

    public WeWorkClient(WeWorkProperties workProperties, WxCpService wxCpService){
        this.properties = workProperties;
        this.wxCpService = wxCpService;
    }

    /**
     * 获取Oauth回调地址
     * @return Oauth回调地址
     */
    public String getOauthRedirectUrl(){
        return properties.getRedirectUrl();
    }

}