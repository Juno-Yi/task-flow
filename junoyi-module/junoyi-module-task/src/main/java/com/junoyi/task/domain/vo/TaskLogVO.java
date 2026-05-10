package com.junoyi.task.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 任务操作记录
 *
 * @author Fan
 */
@Data
public class TaskLogVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务标题
     */
    private String taskTitle;

    /**
     * 操作者Id
     */
    private Long operatorId;

    /**
     * 操作者昵称
     */
    private String operatorNickName;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 操作类型标签
     */
    private String actionTypeLabel;

    /**
     * 操作备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}