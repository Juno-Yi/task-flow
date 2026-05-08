package com.junoyi.oauth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthConfig;
import com.junoyi.oauth.domain.vo.OauthConfigVO;

import java.util.List;


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
    PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO, Page<OauthConfig> page);

    /**
     * 添加 Oauth 配置
     * @param dto 传输对象
     */
    void addOauthConfig(OauthConfigDTO dto);

    /**
     * 更新 Oauth 配置
     * @param dto 传输对象
     */
    void updateOauthConfig(OauthConfigDTO dto);

    /**
     * 删除 Oauth 配置
     * @param id 配置ID
     */
    void deleteOauthConfig(Long id);

    /**
     * 批量删除 Oauth 配置
     * @param ids 配置ID列表
     */
    void deleteOauthConfigBatch(List<Long> ids);
}
