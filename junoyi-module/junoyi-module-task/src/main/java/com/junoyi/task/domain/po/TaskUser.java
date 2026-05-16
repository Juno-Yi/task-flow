package com.junoyi.task.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务-用户关联表
 *
 * @author Fan
 */
@Data
@TableName("task_user")
public class TaskUser {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务Id
     */
    private Long taskId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 任务角色
     */
    private Integer taskRole;

    /**
     * 分配时间
     */
    private Date createTime;
}