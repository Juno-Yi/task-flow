package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthConfig;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.mapper.OauthConfigMapper;
import com.junoyi.oauth.service.IOauthConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Oauth 配置业务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class OauthConfigServiceImpl implements IOauthConfigService {

    private final OauthConfigMapper oauthConfigMapper;

    /**
     * 分页查询Oauth平台配置
     * @param queryDTO 查询条件
     * @param page 分页对象
     * @return Oauth平台配置分页结果
     */
    @Override
    public PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO, Page<OauthConfig> page) {
        return null;
    }

    /**
     * 添加 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    public void addOauthConfig(OauthConfigDTO dto) {

    }

    /**
     * 更新 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    public void updateOauthConfig(OauthConfigDTO dto) {

    }

    /**
     * 删除 Oauth 配置
     * @param id 配置ID
     */
    @Override
    public void deleteOauthConfig(Long id) {

    }

    /**
     * 批量删除 Oauth 配置
     * @param ids 配置ID列表
     */
    @Override
    public void deleteOauthConfigBatch(List<Long> ids) {

    }
}