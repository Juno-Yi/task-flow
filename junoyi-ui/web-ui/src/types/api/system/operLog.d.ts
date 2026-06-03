declare namespace Api {
    namespace System {
        /** 操作日志VO */
        interface SysOperLogVO {
            /** 主键 */
            id: number
            /** 日志级别 */
            level: string
            /** 动作 */
            action: string
            /** 动作名称 */
            actionName: string
            /** 模块 */
            module: string
            /** 模块名称 */
            moduleName: string
            /** 操作用户ID */
            userId: number
            /** 用户名 */
            userName: string
            /** 昵称 */
            nickName: string
            /** 详情描述 */
            message: string
            /** 对象ID */
            targetId: string
            /** 对象名称 */
            targetName: string
            /** 请求路径 */
            path: string
            /** 请求方法 */
            method: string
            /** 操作IP */
            ip: string
            /** 原始数据JSON */
            rawData: string
            /** 操作时间 */
            createTime: string
        }

        /** 操作日志查询参数 */
        interface SysOperLogQueryDTO {
            /** 当前页码 */
            current?: number
            /** 每页条数 */
            size?: number
            /** 日志级别 */
            level?: string
            /** 动作 */
            action?: string
            /** 模块 */
            module?: string
            /** 用户名 */
            userName?: string
            /** 对象ID */
            targetId?: string
            /** 详情关键字 */
            message?: string
            /** 开始时间 */
            startTime?: string
            /** 结束时间 */
            endTime?: string
        }
    }
}
