package com.junoyi.system.domain.dto;

import com.junoyi.system.domain.bo.SysDeptSortItem;
import lombok.Data;

import java.util.List;

/**
 * 系统部门排序
 * (类似系统菜单排序）
 *
 * @author Fan
 */
@Data
public class SysDeptSortDTO {

    /**
     * 排序项列表（用于包装格式）
     */
    private List<SysDeptSortItem> items;
}