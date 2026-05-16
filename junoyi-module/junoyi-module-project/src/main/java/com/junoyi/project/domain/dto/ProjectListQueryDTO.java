package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目列表搜索 DTO 数据实体对象
 *
 * @author Fan
 */
@Data
public class ProjectListQueryDTO {

    /**
     * 项目编号
     */
    private String no;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目类型
     */
    private Integer type;

    /**
     * 项目状态
     */
    private Integer status;
}