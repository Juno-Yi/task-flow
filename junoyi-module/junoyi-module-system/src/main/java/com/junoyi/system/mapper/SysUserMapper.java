package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper 接口类
 *
 * @author Fan
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
