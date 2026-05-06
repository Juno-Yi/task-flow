package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统操作日志 PO 数据实体对象
 *
 * @author Fan
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志级别：info/warn/error
     */
    private String level;

    /**
     * 动作：view/create/update/delete/download/connect/export/import
     */
    private String action;

    /**
     * 模块：vulnerability/webshell/project/user等
     */
    private String module;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 详情描述
     */
    private String message;

    /**
     * 对象ID
     */
    private String targetId;

    /**
     * 对象名称
     */
    private String targetName;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法：GET/POST/PUT/DELETE
     */
    private String method;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 原始数据JSON
     */
    private String rawData;

    /**
     * 操作时间
     */
    private Date createTime;
}