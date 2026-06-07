package com.junoyi.oauth.service;

import com.junoyi.oauth.domain.dto.BindOauthParamsDTO;
import com.junoyi.oauth.domain.vo.ThirdAuthUrlVO;
import com.junoyi.oauth.domain.vo.WeWorkConfigVO;
import com.junoyi.system.domain.vo.AuthVO;

/**
 * 企业微信业务接口
 *
 * @author Fan
 */
public interface IWeWorkService {

    /**
     * 获取企业微信OAuth授权URL
     *
     * @return 授权URL信息
     */
    ThirdAuthUrlVO getAuthorizationUrl();

    /**
     * 获取企业微信登录配置
     *
     * @return 企业微信登录配置
     */
    WeWorkConfigVO getLoginConfig();

    /**
     * 处理企业微信OAuth回调
     *
     * @param code 授权码
     * @return 认证信息
     */
    AuthVO handleCallback(String code);

    /**
     * 绑定企业微信账号
     * @param dto 绑定并登录数据
     * @return 认证信息
     */
    AuthVO bindAccount(BindOauthParamsDTO dto);

}
