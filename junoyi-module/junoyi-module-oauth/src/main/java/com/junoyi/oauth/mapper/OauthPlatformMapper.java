package com.junoyi.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.oauth.domain.po.OauthPlatform;
import org.apache.ibatis.annotations.Mapper;

/**
 * Oauth平台配置 Mapper 接口
 *
 * @author Fan
 */
@Mapper
public interface OauthPlatformMapper extends BaseMapper<OauthPlatform> {
}

