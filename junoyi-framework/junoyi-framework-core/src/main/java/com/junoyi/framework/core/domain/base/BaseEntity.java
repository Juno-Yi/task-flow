package com.junoyi.framework.core.domain.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * BaseEntity类是所有实体类的基类，实现了Serializable接口
 * 用于提供实体对象的基本功能，支持序列化操作
 *
 * @author Fan
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
