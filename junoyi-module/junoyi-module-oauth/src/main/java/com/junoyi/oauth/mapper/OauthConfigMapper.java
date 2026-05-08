package com.junoyi.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.oauth.domain.po.OauthConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * Oauth 配置 Mapper
 *
 * @author Fan
 */
@Mapper
public interface OauthConfigMapper extends BaseMapper<OauthConfig> {
}
