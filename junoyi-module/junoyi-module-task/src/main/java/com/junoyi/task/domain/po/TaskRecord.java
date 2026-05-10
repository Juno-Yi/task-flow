package com.junoyi.task.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务记录表 PO 数据对象
 *
 * @author Fan
 */
@Data
@TableName("task_record")
public class TaskRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 操作者ID
     */
    private Long operatorId;

    /**
     * 任务操作类型
     */
    private Integer actionType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}