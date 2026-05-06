package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysOperLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统操作日志 Mapper
 *
 * @author Fan
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 清空登录日志（管理员操作）
     */
    @InterceptorIgnore(blockAttack = "true")
    @Delete("TRUNCATE TABLE sys_oper_log")
    void truncate();
}
