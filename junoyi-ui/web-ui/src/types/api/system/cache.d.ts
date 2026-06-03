/**
 * 缓存监控相关类型定义
 */
declare namespace Api {
  namespace System {
    /** Redis 服务器信息 */
    interface RedisInfo {
      /** Redis 版本 */
      version: string
      /** 运行模式 */
      mode: string
      /** 运行时间（秒） */
      uptimeInSeconds: number
      /** 已连接客户端数 */
      connectedClients: number
      /** 已使用内存 */
      usedMemory: string
      /** 已使用内存（人类可读） */
      usedMemoryHuman: string
      /** 内存峰值 */
      usedMemoryPeak: string
      /** 内存峰值（人类可读） */
      usedMemoryPeakHuman: string
      /** 数据库键数量 */
      dbSize: number
      /** 命中次数 */
      keyspaceHits: number
      /** 未命中次数 */
      keyspaceMisses: number
      /** 命中率 */
      hitRate: string
      /** 每秒执行命令数 */
      instantaneousOpsPerSec: number
      /** 网络输入（KB） */
      totalNetInputBytes: string
      /** 网络输出（KB） */
      totalNetOutputBytes: string
    }

    /** 缓存键信息 */
    interface CacheKeyVO {
      /** 键名 */
      key: string
      /** 值类型 */
      type: 'string' | 'list' | 'set' | 'zset' | 'hash'
      /** TTL（秒），-1 表示永不过期，-2 表示已过期 */
      ttl: number
      /** 内存占用（字节） */
      memoryUsage?: number
      /** 值大小 */
      size?: number
    }

    /** 缓存键值详情 */
    interface CacheKeyDetailVO extends CacheKeyVO {
      /** 值内容 */
      value: string | string[] | Record<string, string>
    }

    /** 缓存键列表查询参数 */
    interface CacheKeyQueryParams extends Api.Common.PaginationParams {
      /** 键名模式（支持通配符） */
      pattern?: string
      /** 值类型 */
      type?: string
    }

    /** 缓存键列表响应 */
    type CacheKeyListResponse = Api.Common.PaginatedResponse<CacheKeyVO>
  }
}
