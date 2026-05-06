package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 权限组查询传输数据
 *
 * @author Fan
 */
@Data
public class SysPermGroupQueryDTO {

    /**
     * 权限组编码
     */
    private String groupCode;

    /**
     * 权限组名称
     */
    private String groupName;

    /**
     * 状态
     */
    private Integer status;
}
