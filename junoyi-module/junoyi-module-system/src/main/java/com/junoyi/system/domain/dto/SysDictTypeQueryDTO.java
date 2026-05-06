package com.junoyi.system.domain.dto;

import com.junoyi.framework.core.domain.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询DTO
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictTypeQueryDTO extends PageQuery {

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
}
