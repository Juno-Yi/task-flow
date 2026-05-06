package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.system.domain.dto.SysDictDataDTO;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 字典数据转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring")
public interface SysDictDataConverter extends BaseConverter<SysDictDataDTO, SysDictData, SysDictDataVO> {

    /**
     * PO转VO - 直接映射所有字段
     */
    @Override
    SysDictDataVO toVo(SysDictData entity);

    /**
     * DTO转PO
     */
    @Override
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysDictData toEntity(SysDictDataDTO dto);

    /**
     * DTO转VO - 直接映射
     */
    @Override
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysDictDataVO dtoToVo(SysDictDataDTO dto);

    /**
     * 更新实体
     */
    @Override
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(SysDictDataDTO dto, @org.mapstruct.MappingTarget SysDictData entity);
}
