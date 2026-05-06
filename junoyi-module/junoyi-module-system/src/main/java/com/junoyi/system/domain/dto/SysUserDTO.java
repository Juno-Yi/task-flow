package com.junoyi.system.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 系统用户传输数据
 *
 * @author Fan
 */
@Data
public class SysUserDTO {

    /**
     * 用户ID（修改时必填）
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码（添加时必填）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别（0-男，1-女）
     */
    private String sex;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}