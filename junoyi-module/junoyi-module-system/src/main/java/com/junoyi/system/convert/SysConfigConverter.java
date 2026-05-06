package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.po.SysConfig;
import com.junoyi.system.domain.vo.SysConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 系统参数转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring")
public interface SysConfigConverter extends BaseConverter<SysConfigDTO, SysConfig, SysConfigVO> {

    /**
     * PO转VO - 直接映射所有字段，isSystem(0/1)转换为isSystem(Y/N)
     */
    @Override
    @Mapping(target = "isSystem", expression = "java(entity.getIsSystem() != null && entity.getIsSystem() == 1 ? \"Y\" : \"N\")")
    SysConfigVO toVo(SysConfig entity);

    /**
     * DTO转PO - isSystem(Y/N)转换为isSystem(0/1)
     */
    @Override
    @Mapping(target = "isSystem", expression = "java(\"Y\".equals(dto.getIsSystem()) ? 1 : 0)")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysConfig toEntity(SysConfigDTO dto);

    /**
     * DTO转VO - 直接映射
     */
    @Override
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysConfigVO dtoToVo(SysConfigDTO dto);

    /**
     * 更新实体 - isSystem(Y/N)转换为isSystem(0/1)
     */
    @Override
    @Mapping(target = "isSystem", expression = "java(\"Y\".equals(dto.getIsSystem()) ? 1 : 0)")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(SysConfigDTO dto, @org.mapstruct.MappingTarget SysConfig entity);
}
