package com.junoyi.system.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志查询 DTO 数据实体对象
 *
 * @author Fan
 */
@Data
public class SysAuthLogQueryDTO {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录方式
     */
    private String loginType;

    /**
     * 登录状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
