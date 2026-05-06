package com.junoyi.system.domain.vo;

import lombok.Data;

/**
 * Redis 服务器信息 VO
 *
 * @author Fan
 */
@Data
public class RedisInfoVO {

    /**
     * Redis 版本
     */
    private String version;

    /**
     * 运行模式
     */
    private String mode;

    /**
     * 运行时间（秒）
     */
    private Long uptimeInSeconds;

    /**
     * 已连接客户端数
     */
    private Integer connectedClients;

    /**
     * 已使用内存
     */
    private String usedMemory;

    /**
     * 已使用内存（人类可读）
     */
    private String usedMemoryHuman;

    /**
     * 内存峰值
     */
    private String usedMemoryPeak;

    /**
     * 内存峰值（人类可读）
     */
    private String usedMemoryPeakHuman;

    /**
     * 数据库键数量
     */
    private Long dbSize;

    /**
     * 命中次数
     */
    private Long keyspaceHits;

    /**
     * 未命中次数
     */
    private Long keyspaceMisses;

    /**
     * 命中率
     */
    private String hitRate;

    /**
     * 每秒执行命令数
     */
    private Long instantaneousOpsPerSec;

    /**
     * 网络输入（KB）
     */
    private String totalNetInputBytes;

    /**
     * 网络输出（KB）
     */
    private String totalNetOutputBytes;
}
