package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 项目成员关联表 PO 数据实体对象
 *
 * @author Fan
 */
@Data
@TableName("project_member")
public class ProjectMember {

    /** 主键ID */
    @TableId
    private Long id;

    /** 项目ID */
    private Long projectId;

    /** 用户ID */
    private Long userId;

    /** 项目角色 */
    private String role;

    /** 状态 */
    private Integer status;

    /** 加入时间 */
    private Date joinTime;

    /** 离开时间 */
    private Date leaveTime;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}