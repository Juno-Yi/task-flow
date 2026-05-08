package com.junoyi.oauth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthPlatform;
import com.junoyi.oauth.domain.vo.OauthConfigVO;

/**
 * Oauth配置业务接口
 *
 * @author Fan
 */
public interface IOauthConfigService {

    /**
     * 分页查询Oauth平台配置
     * @param queryDTO 查询条件
     * @param page 分页对象
     * @return Oauth平台配置分页结果
     */
    PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO, Page<OauthPlatform> page);
}
