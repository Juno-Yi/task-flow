package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目下拉列表查询参数 DTO
 *
 * @author Fan
 */
@Data
public class ProjectOptionQueryDTO {

    /**
     * 项目编号
     */
    private String no;

    /**
     * 项目名
     */
    private String name;
}