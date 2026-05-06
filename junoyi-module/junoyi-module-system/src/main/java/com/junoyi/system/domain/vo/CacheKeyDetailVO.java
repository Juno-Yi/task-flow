package com.junoyi.system.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 缓存键详情 VO（包含值）
 *
 * @author Fan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CacheKeyDetailVO extends CacheKeyVO {

    /**
     * 缓存值（JSON 字符串）
     */
    private Object value;
}
