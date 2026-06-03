/**
 * 登录日志相关类型定义
 */
declare namespace Api.System {
    /** 登录日志VO */
    interface AuthLogVO {
        /** 日志ID */
        id: number
        /** 用户ID */
        userId: number | null
        /** 用户名 */
        userName: string
        /** 用户昵称 */
        nickName: string | null
        /** 登录IP */
        loginIp: string
        /** IP归属地 */
        ipRegion: string | null
        /** 会话ID */
        sessionId: string | null
        /** 身份（角色名称） */
        identity: string | null
        /** 登录方式 */
        loginType: string
        /** 登录方式名称 */
        loginTypeName: string
        /** 浏览器 */
        browser: string | null
        /** 操作系统 */
        os: string | null
        /** 设备类型 */
        deviceType: string | null
        /** 登录状态（0-失败，1-成功） */
        status: number
        /** 提示消息 */
        msg: string | null
        /** 登录时间 */
        loginTime: string
    }

    /** 登录日志查询参数 */
    interface AuthLogQueryDTO {
        /** 用户名 */
        userName?: string
        /** 用户昵称 */
        nickName?: string
        /** 登录IP */
        loginIp?: string
        /** 登录方式 */
        loginType?: string
        /** 登录状态 */
        status?: number
        /** 开始时间 */
        startTime?: string
        /** 结束时间 */
        endTime?: string
        /** 当前页 */
        current?: number
        /** 每页条数 */
        size?: number
    }
}
