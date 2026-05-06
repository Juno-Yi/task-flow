package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.SysMenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 系统菜单转换器接口
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface SysMenuConverter extends BaseConverter<SysMenuDTO, SysMenu, SysMenuVO> {

    SysMenuConverter INSTANCE = Mappers.getMapper(SysMenuConverter.class);
}
