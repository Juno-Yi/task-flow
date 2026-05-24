package com.junoyi.project.domain.vo;

import lombok.Data;

/**
 * 项目需求情况 VO 数据对象
 */
@Data
public class ProjectRequirementSituationVO {

    /**
     * 状态编码
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusLabel;

    /**
     * 状态标签类型
     */
    private String statusType;

    /**
     * 数量
     */
    private Long count;
}