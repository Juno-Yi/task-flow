package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 项目动态记录 PO 数据实体对象
 *
 * @author Fan
 */
@Data
@TableName("project_record")
public class ProjectRecord {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作动态类型
     */
    private Integer type;

    /**
     * 操作目标类型
     */
    private Integer targetType;

    /**
     * 操作目标ID
     */
    private Integer targetId;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;
}