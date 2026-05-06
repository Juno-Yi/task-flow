package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统角色数据访问层接口
 * 该接口继承BaseMapper，提供SysRole实体的基本增删改查操作
 * 使用@Mapper注解标识为MyBatis-Plus的Mapper接口
 *
 * @author Fan
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

}
