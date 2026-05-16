package com.junoyi.project.domain.vo;

import lombok.Data;

/**
 * 项目信息 VO 数据对象
 *
 * @author Fan
 */
@Data
public class ProjectInfoVO {

    /**
     * 项目ID
     */
    private Long id;

    /**
     * 项目编号
     */
    private String no;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目类型
     */
    private Integer type;

    private String typeLabel;

    private String typeLabelType;
}