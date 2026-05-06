package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.SysRoleDTO;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.vo.SysRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统角色转换器接口
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface SysRoleConverter {

    SysRoleConverter INSTANCE = Mappers.getMapper(SysRoleConverter.class);

    SysRoleVO toVo(SysRole sysRole);

    List<SysRoleVO> toVoList(List<SysRole> sysRoleList);

    SysRole toPo(SysRoleDTO roleDTO);
}
