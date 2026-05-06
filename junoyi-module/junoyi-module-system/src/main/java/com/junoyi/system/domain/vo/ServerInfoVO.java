package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务器信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfoVO {
    
    /**
     * 服务器名称
     */
    private String name;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 系统架构
     */
    private String arch;
    
    /**
     * CPU核心数
     */
    private Integer cpuCores;
    
    /**
     * 服务器IP
     */
    private String ip;
    
    /**
     * 服务器时间
     */
    private String time;
}
