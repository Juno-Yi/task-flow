package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户独立权限VO
 *
 * @author Fan
 */
@Data
public class SysUserPermVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 权限字符串
     */
    private String permission;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
