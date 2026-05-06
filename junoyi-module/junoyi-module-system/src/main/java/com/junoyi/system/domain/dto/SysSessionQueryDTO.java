package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 会话查询DTO
 *
 * @author Fan
 */
@Data
public class SysSessionQueryDTO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 平台类型代码
     */
    private Integer platformType;
}
