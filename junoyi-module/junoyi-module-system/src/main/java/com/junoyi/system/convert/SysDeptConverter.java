package com.junoyi.system.convert;

import com.junoyi.framework.core.convert.MapStructConfig;
import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.vo.SysDeptVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统部门转换器接口
 *
 * @author Fan
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface SysDeptConverter {

    SysDeptConverter INSTANCE = Mappers.getMapper(SysDeptConverter.class);

    SysDeptVO toVo(SysDept sysDept);

    List<SysDeptVO> toVoList(List<SysDept> sysDeptList);

    SysDept toPo(SysDeptDTO dto);
}
