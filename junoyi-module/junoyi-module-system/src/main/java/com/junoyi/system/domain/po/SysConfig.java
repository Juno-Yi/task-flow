package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置数据实体类
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId
    private Long configId;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置键值
     */
    private String configValue;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型（text/number/boolean/json）
     */
    private String configType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否系统内置（0否 1是）
     */
    private Integer isSystem;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
}