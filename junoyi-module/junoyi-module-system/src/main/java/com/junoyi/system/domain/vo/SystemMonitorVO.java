package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 系统监控信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMonitorVO {
    
    /**
     * 系统信息
     */
    private SystemBasicInfoVO systemInfo;
    
    /**
     * 服务器信息
     */
    private ServerInfoVO serverInfo;
    
    /**
     * Java信息
     */
    private JavaInfoVO javaInfo;
    
    /**
     * 内存信息
     */
    private MemoryInfoVO memoryInfo;
    
    /**
     * 磁盘信息列表
     */
    private List<DiskInfoVO> diskInfo;
}
