package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 项目仓库表 PO 数据实体对象
 *
 * @author Fan
 */
@Data
@TableName("project_repository")
public class ProjectRepository {

    /** 仓库ID */
    @TableId
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

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;
}
