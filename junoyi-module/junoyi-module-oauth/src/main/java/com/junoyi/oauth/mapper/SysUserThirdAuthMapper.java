package com.junoyi.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.oauth.domain.po.SysUserThirdAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户第三方登录绑定 Mapper
 */
@Mapper
public interface SysUserThirdAuthMapper extends BaseMapper<SysUserThirdAuth> {
}

