package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目 DTO 数据实体对象
 *
 * @author Fan
 */
@Data
public class ProjectListDTO {

    /** 项目ID  */
    private Long id;

    /** 项目编号 */
    private String no;

    /** 项目名称 */
    private String name;

    /** 项目介绍 */
    private String description;

    /** 项目负责人 */
    private Long leader;

    /** 项目类型 */
    private Integer type;

    /** 项目状态 */
    private Integer status;

    /** 项目优先级 */
    private Integer priority;

    /** 项目备注 */
    private String remark;
}