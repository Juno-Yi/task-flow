package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户与部门关联数据实体
 *
 * @author Fan
 */
@Data
@TableName("sys_user_dept")
public class SysUserDept {

    @TableId
    private Long id;

    private Long userId;

    private Long deptId;
}