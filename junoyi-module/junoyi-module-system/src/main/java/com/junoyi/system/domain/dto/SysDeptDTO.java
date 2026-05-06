package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 系统部门传输数据
 *
 * @author Fan
 */
@Data
public class SysDeptDTO {

    /**
     * 部门ID（修改时必填）
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

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
     * 部门状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
