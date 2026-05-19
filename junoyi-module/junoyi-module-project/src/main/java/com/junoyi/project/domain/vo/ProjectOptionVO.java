package com.junoyi.project.domain.vo;

import lombok.Data;

/**
 * 项目下拉选项 VO
 *
 * @author Fan
 */
@Data
public class ProjectOptionVO {

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
     * 项目状态
     */
    private Integer status;

    /**
     * 项目优先级
     */
    private Integer priority;
}