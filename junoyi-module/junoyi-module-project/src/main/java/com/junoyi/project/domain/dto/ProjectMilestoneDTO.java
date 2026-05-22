package com.junoyi.project.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 项目里程碑 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class ProjectMilestoneDTO {

    /**
     * 项目里程碑主键ID（修改时候使用）
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 里程碑名称
     */
    private String name;

    /**
     * 里程碑介绍
     */
    private String description;

    /**
     * 截止时间
     */
    private Date dueTime;

    /**
     * 里程碑排序
     */
    private Integer sort;

    /**
     * 项目里程碑负责人
     */
    private Long ownerId;
}