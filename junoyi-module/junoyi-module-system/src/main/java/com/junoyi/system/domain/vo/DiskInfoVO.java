package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 磁盘信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiskInfoVO {
    
    /**
     * 挂载路径
     */
    private String path;
    
    /**
     * 文件系统类型
     */
    private String type;
    
    /**
     * 总容量
     */
    private String total;
    
    /**
     * 已用容量
     */
    private String used;
    
    /**
     * 空闲容量
     */
    private String free;
    
    /**
     * 使用率
     */
    private Integer usedPercent;
}
