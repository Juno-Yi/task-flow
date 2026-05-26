package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 项目动态记录 VO
 *
 * @author Fan
 */
@Data
public class ProjectRecordVO {

    /**
     * ID主键
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目标题
     */
    private String projectTitle;

    /**
     * 操作者ID
     */
    private Long operatorId;

    /**
     * 操作者昵称
     */
    private String operatorNickName;

    /**
     * 操作类型
     */
    private Integer type;

    /**
     * 操作类型标签
     */
    private String typeLabel;

    /**
     * 操作目标类型
     */
    private Integer targetType;

    /**
     * 操作目标类型标签
     */
    private String targetTypeLabel;

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