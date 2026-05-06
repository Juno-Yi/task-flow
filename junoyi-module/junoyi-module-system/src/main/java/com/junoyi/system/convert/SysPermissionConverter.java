package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.BaseConverter;
import com.junoyi.system.domain.dto.SysPermissionDTO;
import com.junoyi.system.domain.po.SysPermission;
import com.junoyi.system.domain.vo.SysPermissionVO;
import org.mapstruct.Mapper;

/**
 * 权限池对象转换器
 *
 * @author Fan
 */
@Mapper(componentModel = "spring")
public interface SysPermissionConverter extends BaseConverter<SysPermissionDTO, SysPermission, SysPermissionVO> {
}
