package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统基本信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemBasicInfoVO {
    
    /**
     * 系统名称
     */
    private String name;
    
    /**
     * 系统版本
     */
    private String version;
    
    /**
     * 框架版本
     */
    private String frameworkVersion;
    
    /**
     * 运行环境
     */
    private String environment;
    
    /**
     * 启动时间
     */
    private String startTime;
    
    /**
     * 运行时长
     */
    private String uptime;
}
