/**
 * 系统监控相关类型定义
 */
declare namespace Api.System {
  /**
   * 系统基本信息
   */
  interface SystemBasicInfoVO {
    /** 系统名称 */
    name: string
    /** 系统版本 */
    version: string
    /** 框架版本 */
    frameworkVersion: string
    /** 运行环境 */
    environment: string
    /** 启动时间 */
    startTime: string
    /** 运行时长 */
    uptime: string
  }

  /**
   * 服务器信息
   */
  interface ServerInfoVO {
    /** 服务器名称 */
    name: string
    /** 操作系统 */
    os: string
    /** 系统架构 */
    arch: string
    /** CPU核心数 */
    cpuCores: number
    /** 服务器IP */
    ip: string
    /** 服务器时间 */
    time: string
  }

  /**
   * Java信息
   */
  interface JavaInfoVO {
    /** Java版本 */
    version: string
    /** Java供应商 */
    vendor: string
    /** Java安装路径 */
    home: string
    /** JVM名称 */
    jvmName: string
    /** JVM版本 */
    jvmVersion: string
    /** JVM启动参数 */
    args: string
  }

  /**
   * 内存信息
   */
  interface MemoryInfoVO {
    /** 系统总内存 */
    total: string
    /** 系统已用内存 */
    used: string
    /** 系统空闲内存 */
    free: string
    /** 系统内存使用率 */
    usedPercent: number
    /** JVM总内存 */
    jvmTotal: string
    /** JVM已用内存 */
    jvmUsed: string
    /** JVM空闲内存 */
    jvmFree: string
    /** JVM内存使用率 */
    jvmUsedPercent: number
  }

  /**
   * 磁盘信息
   */
  interface DiskInfoVO {
    /** 挂载路径 */
    path: string
    /** 文件系统类型 */
    type: string
    /** 总容量 */
    total: string
    /** 已用容量 */
    used: string
    /** 空闲容量 */
    free: string
    /** 使用率 */
    usedPercent: number
  }

  /**
   * 系统监控信息
   */
  interface SystemMonitorVO {
    /** 系统信息 */
    systemInfo: SystemBasicInfoVO
    /** 服务器信息 */
    serverInfo: ServerInfoVO
    /** Java信息 */
    javaInfo: JavaInfoVO
    /** 内存信息 */
    memoryInfo: MemoryInfoVO
    /** 磁盘信息列表 */
    diskInfo: DiskInfoVO[]
  }
}
