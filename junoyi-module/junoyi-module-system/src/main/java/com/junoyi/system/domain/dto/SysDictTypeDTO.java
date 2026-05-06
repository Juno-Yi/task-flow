package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 字典类型DTO
 *
 * @author Fan
 */
@Data
public class SysDictTypeDTO {

    /**
     * 字典主键（修改时必填）
     */
    private Long dictId;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}