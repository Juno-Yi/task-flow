package com.junoyi.system.domain.dto;

import com.junoyi.framework.core.domain.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据查询DTO
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictDataQueryDTO extends PageQuery {

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}
