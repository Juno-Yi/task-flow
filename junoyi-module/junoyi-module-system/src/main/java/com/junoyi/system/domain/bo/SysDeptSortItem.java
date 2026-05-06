package com.junoyi.system.domain.bo;

import lombok.Data;

/**
 * 部门排序项
 *
 * @author Fan
 */
@Data
public class SysDeptSortItem {

    /**
     * 部门 ID
     */
    private Long id;

    /**
     * 父级部门 ID
     */
    private  Long parentId;

    /**
     * 排序值
     */
    private Integer sort;

}