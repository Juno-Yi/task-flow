package com.junoyi.system.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统操作日志查询 DTO 数据实体对象
 *
 * @author Fan
 */
@Data
public class SysOperLogQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 动作
     */
    private String action;

    /**
     * 模块
     */
    private String module;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 对象ID
     */
    private String targetId;

    /**
     * 详情关键字
     */
    private String message;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}