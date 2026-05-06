package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysUserPerm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户独立权限 Mapper
 *
 * @author Fan
 */
@Mapper
public interface SysUserPermMapper extends BaseMapper<SysUserPerm> {
}
