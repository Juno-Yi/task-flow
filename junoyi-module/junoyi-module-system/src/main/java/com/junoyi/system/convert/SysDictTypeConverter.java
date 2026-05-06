package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.system.domain.dto.SysDictTypeDTO;
import com.junoyi.system.domain.po.SysDictType;
import com.junoyi.system.domain.vo.SysDictTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 字典类型转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring")
public interface SysDictTypeConverter extends BaseConverter<SysDictTypeDTO, SysDictType, SysDictTypeVO> {

    /**
     * PO转VO - 直接映射所有字段
     */
    @Override
    SysDictTypeVO toVo(SysDictType entity);

    /**
     * DTO转PO
     */
    @Override
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysDictType toEntity(SysDictTypeDTO dto);

    /**
     * DTO转VO - 直接映射
     */
    @Override
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysDictTypeVO dtoToVo(SysDictTypeDTO dto);

    /**
     * 更新实体
     */
    @Override
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(SysDictTypeDTO dto, @org.mapstruct.MappingTarget SysDictType entity);
}
