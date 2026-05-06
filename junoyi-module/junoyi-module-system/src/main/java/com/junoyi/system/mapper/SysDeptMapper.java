package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统部门数据访问层接口
 * 该接口继承自BaseMapper，提供对SysDept实体的基本CRUD操作
 * 通过@Mapper注解标识为MyBatis-Plus的Mapper接口
 *
 * @author Fan
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
}

