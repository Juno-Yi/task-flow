package com.junoyi.oauth.convert;

import com.junoyi.oauth.domain.po.OauthPlatform;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Oauth配置转换器
 *
 * @author Fan
 */
@Mapper
public interface OauthConfigConverter {

    OauthConfigConverter INSTANCE = Mappers.getMapper(OauthConfigConverter.class);

    /**
     * PO转VO
     */
    @Mapping(source = "redirectUri", target = "redirectUrl")
    OauthConfigVO toVO(OauthPlatform oauthPlatform);
}

