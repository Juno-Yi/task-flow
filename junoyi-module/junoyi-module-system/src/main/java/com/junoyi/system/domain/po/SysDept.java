package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户部门数据实体对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /**
     * 部门主键 ID
     */
    @TableId
    private Long id;

    /**
     * 父部门 ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 排序
     */
    private int sort;

    /**
     * 负责人名称
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phonenumber;

    /**
     * 部门邮箱
     */
    private String email;

    /**
     * 部门状态
     */
    private int status;

    /**
     * 删除标识（软删除）
     */
    private boolean delFlag;


}