package com.junoyi.task.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务附件 Mapper
 *
 * @author Fan
 */
@Data
@TableName("task_attachment")
public class TaskAttachment {

    /**
     * 任务附件主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务Id
     */
    private Long taskId;

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 文件名字
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String fileUrl;

    /**
     * 上传用户名
     */
    private Long uploadUser;

    /**
     * 创建时间
     */
    private Date createTime;
}