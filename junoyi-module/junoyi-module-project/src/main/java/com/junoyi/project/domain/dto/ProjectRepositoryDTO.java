package com.junoyi.project.domain.dto;

import lombok.Data;

/**
 * 项目仓库 DTO 数据实体对象
 *
 * @author Fan
 */
@Data
public class ProjectRepositoryDTO {

    /** 仓库ID */
    private Long id;

    /** 项目ID */
    private Long projectId;

    /** 仓库名称 */
    private String name;

    /** 仓库平台：gitee/github/gitlab/custom */
    private String type;

    /** 仓库地址 */
    private String url;

    /** 默认分支 */
    private String branch;

    /** 仓库描述 */
    private String description;

    /** 是否主仓库：0-否 1-是 */
    private Boolean isMain;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
