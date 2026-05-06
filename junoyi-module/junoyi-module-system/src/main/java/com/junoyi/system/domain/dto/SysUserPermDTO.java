package com.junoyi.system.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户独立权限DTO
 *
 * @author Fan
 */
@Data
public class SysUserPermDTO {

    /**
     * 权限字符串列表
     */
    private List<String> permissions;

    /**
     * 过期时间（可选，为空表示永不过期）
     */
    private Date expireTime;
}
