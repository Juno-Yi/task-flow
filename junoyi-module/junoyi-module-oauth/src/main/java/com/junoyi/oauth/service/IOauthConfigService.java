package com.junoyi.oauth.service;

import com.junoyi.oauth.domain.vo.OauthConfigVO;

import java.util.List;

/**
 * Oauth配置业务接口
 *
 * @author Fan
 */
public interface IOauthConfigService {

    /**
     * 获取Oauth平台配置
     * @return Oauth平台配置列表
     */
    List<OauthConfigVO> getOauthConfigList();
}
