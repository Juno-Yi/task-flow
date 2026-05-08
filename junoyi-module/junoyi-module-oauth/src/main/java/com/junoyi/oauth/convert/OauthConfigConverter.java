package com.junoyi.oauth.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.po.OauthConfig;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * OAuth配置转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring")
public interface OauthConfigConverter extends BaseConverter<OauthConfigDTO, OauthConfig, OauthConfigVO> {

    /**
     * PO转VO - redirectUri 映射到 redirectUrl
     */
    @Override
    @Mapping(source = "redirectUri", target = "redirectUrl")
    OauthConfigVO toVo(OauthConfig entity);

    /**
     * DTO转PO - redirectUrl 映射到 redirectUri
     */
    @Override
    @Mapping(source = "redirectUrl", target = "redirectUri")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    OauthConfig toEntity(OauthConfigDTO dto);

    /**
     * DTO转VO - redirectUrl 直接映射
     */
    @Override
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "platformLabel", ignore = true)
    @Mapping(target = "statusLabel", ignore = true)
    @Mapping(target = "statusType", ignore = true)
    OauthConfigVO dtoToVo(OauthConfigDTO dto);

    /**
     * 更新实体 - redirectUrl 映射到 redirectUri
     */
    @Override
    @Mapping(source = "redirectUrl", target = "redirectUri")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(OauthConfigDTO dto, @org.mapstruct.MappingTarget OauthConfig entity);
}

