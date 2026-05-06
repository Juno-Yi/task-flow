package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内存信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryInfoVO {
    
    /**
     * 系统总内存
     */
    private String total;
    
    /**
     * 系统已用内存
     */
    private String used;
    
    /**
     * 系统空闲内存
     */
    private String free;
    
    /**
     * 系统内存使用率
     */
    private Integer usedPercent;
    
    /**
     * JVM总内存
     */
    private String jvmTotal;
    
    /**
     * JVM已用内存
     */
    private String jvmUsed;
    
    /**
     * JVM空闲内存
     */
    private String jvmFree;
    
    /**
     * JVM内存使用率
     */
    private Integer jvmUsedPercent;
}
