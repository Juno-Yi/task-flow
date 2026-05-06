/**
 * 会话监控相关类型定义
 */
declare namespace Api.System {
  /** 平台类型枚举 */
  type PlatformType = 'ADMIN_WEB' | 'FRONT_DESK_WEB' | 'MINI_PROGRAM' | 'APP' | 'DESKTOP_APP'

  /** 设备类型 */
  type DeviceType = 'Mobile' | 'Tablet' | 'Desktop' | 'Unknown'

  /** 会话信息 */
  interface SessionVO {
    /** 会话ID */
    sessionId: string
    /** 用户ID */
    userId: number
    /** 用户名 */
    userName: string
    /** 用户昵称 */
    nickName: string
    /** 平台类型 */
    platformType: PlatformType
    /** 登录IP */
    loginIp: string
    /** IP所在地区 */
    ipRegion: string | null
    /** 登录时间 */
    loginTime: string
    /** 最后访问时间 */
    lastAccessTime: string
    /** 用户代理（浏览器信息） */
    userAgent: string | null
    /** 设备类型 */
    deviceType: DeviceType | null
    /** 操作系统 */
    os: string | null
    /** 浏览器 */
    browser: string | null
    /** AccessToken 过期时间（时间戳） */
    accessExpireTime: number
    /** RefreshToken 过期时间（时间戳） */
    refreshExpireTime: number
  }

  /** 会话查询参数 */
  interface SessionQueryDTO {
    /** 用户名 */
    userName?: string
    /** 昵称 */
    nickName?: string
    /** 登录IP */
    loginIp?: string
    /** 平台类型 */
    platformType?: PlatformType
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }
}
