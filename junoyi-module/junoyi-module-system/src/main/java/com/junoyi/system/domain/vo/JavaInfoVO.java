package com.junoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Java信息VO
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JavaInfoVO {
    
    /**
     * Java版本
     */
    private String version;
    
    /**
     * Java供应商
     */
    private String vendor;
    
    /**
     * Java安装路径
     */
    private String home;
    
    /**
     * JVM名称
     */
    private String jvmName;
    
    /**
     * JVM版本
     */
    private String jvmVersion;
    
    /**
     * JVM启动参数
     */
    private String args;
}
