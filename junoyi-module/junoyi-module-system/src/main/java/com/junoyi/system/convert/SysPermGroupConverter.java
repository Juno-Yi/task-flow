package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.SysPermGroupDTO;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 权限组转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface SysPermGroupConverter {

    SysPermGroupConverter INSTANCE = Mappers.getMapper(SysPermGroupConverter.class);

    SysPermGroupVO toVo(SysPermGroup permGroup);

    List<SysPermGroupVO> toVoList(List<SysPermGroup> permGroupList);

    SysPermGroup toPo(SysPermGroupDTO dto);
}
