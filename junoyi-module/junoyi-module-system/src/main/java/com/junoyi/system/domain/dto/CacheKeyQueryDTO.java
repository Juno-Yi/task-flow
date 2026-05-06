package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 缓存键查询 DTO
 *
 * @author Fan
 */
@Data
public class CacheKeyQueryDTO {
    /**
     * 键名模式（支持通配符 *）
     */
    private String pattern;

    /**
     * 值类型：string、list、set、zset、hash
     */
    private String type;
}
