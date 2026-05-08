package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysUserThirdAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户第三方登录绑定 Mapper
 *
 * @author JunoYi
 */
@Mapper
public interface SysUserThirdAuthMapper extends BaseMapper<SysUserThirdAuth> {
}

