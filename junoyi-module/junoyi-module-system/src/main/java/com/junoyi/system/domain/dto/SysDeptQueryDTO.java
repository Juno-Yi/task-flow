package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 系统部门查询数据传输对象
 * 用于封装部门查询时的查询条件参数
 *
 * @author Fan
 */
@Data
public class SysDeptQueryDTO {

    /**
     * 部门名称查询条件
     */
    private String name;

    /**
     * 负责人查询条件
     */
    private String leader;

    /**
     * 联系电话查询条件
     */
    private String phonenumber;

    /**
     * 邮箱查询条件
     */
    private String email;

    /**
     * 状态查询条件
     */
    private Integer status;
}
