package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限池权限实体
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    @TableId("id")
    private Long id;

    private String permission;

    private String description;

    private Integer status;

}