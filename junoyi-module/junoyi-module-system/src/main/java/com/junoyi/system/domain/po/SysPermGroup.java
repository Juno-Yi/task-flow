package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 权限组实体
 *
 * @author Fan
 */
@Data
@TableName(value = "sys_perm_group", autoResultMap = true)
public class SysPermGroup extends BaseEntity {

    @TableId
    private Long id;

    /**
     * 权限组编码
     */
    private String groupCode;

    /**
     * 权限组名称
     */
    private String groupName;

    /**
     * 优先级，用于排序显示
     */
    private Integer priority;

    /**
     * 权限组描述信息
     */
    private String description;

    /**
     * 状态标识，用于控制权限组的启用/禁用状态
     */
    private Integer status;

    /**
     * 权限集合（JSON字段）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> permissions;

}
