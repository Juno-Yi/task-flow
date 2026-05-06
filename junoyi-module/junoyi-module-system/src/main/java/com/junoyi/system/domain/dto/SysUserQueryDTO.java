package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 系统用户查询数据传输对象
 * 用于封装系统用户查询时的查询条件
 *
 * @author Fan
 */
@Data
public class SysUserQueryDTO {

    /**
     * 部门查询条件
     */
    private Long deptId;

    /**
     * 用户名查询条件
     */
    private String userName;

    /**
     * 昵称查询条件
     */
    private String nickName;

    /**
     * 邮箱查询条件
     */
    private String email;

    /**
     * 手机号查询条件
     */
    private String phonenumber;

    /**
     * 性别查询条件
     */
    private String sex;

    /**
     * 状态查询条件
     */
    private Integer status;

}
