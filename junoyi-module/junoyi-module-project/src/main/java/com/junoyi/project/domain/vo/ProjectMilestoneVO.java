package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 项目里程碑 VO
 *
 * @author Fan
 */
@Data
public class ProjectMilestoneVO {

    /**
     * 项目里程碑主键ID
     */
    private Long id;

    /**
     * 项目里程碑名称
     */
    private String name;

    /**
     * 项目里程碑描述
     */
    private String description;

    /**
     * 项目里程碑状态
     */
    private Integer status;

    private String statusLabel;

    private String statusType;

    /**
     * 截止时间
     */
    private Date dueTime;

    /**
     * 实际结束完成时间
     */
    private Date finishTime;

    /**
     * 里程碑排序
     */
    private Integer sort;

    /**
     * 里程碑负责人
     */
    private Long ownerId;

    /**
     * 里程碑负责人昵称
     */
    private String nickName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}