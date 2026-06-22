declare namespace Api.Notification {

    /**
     * 通知列表 VO
     */
    interface NotificationListVO {
        id: number
        title: string
        summary?: string
        content: string
        type: number
        typeLabel: string
        typeType: string
        status: number
        statusLabel: string
        statusType: string
        senderId: number
        senderNickName: string
        publishTime: string
        updateTime: string
    }

    /**
     * 通知详情 VO
     */
    interface NotificationDetailVO {
        id: number
        title: string
        summary?: string
        content: string
        type: number
        typeLabel: string
        typeType: string
        status: number
        statusLabel: string
        statusType: string
        senderId: number
        senderNickName: string
        targetType: number
        targetIds?: number[]
        publishTime: string
        updateTime: string
    }

    /**
     * 通知 DTO（添加/修改）
     */
    interface NotificationDTO {
        /** 主键ID（修改时使用） */
        id?: number
        /** 通知标题 */
        title: string
        /** 通知概况 */
        summary?: string
        /** 通知内容 */
        content?: string
        /** 通知类型 */
        type: number
        /** 状态（0-草稿 1-已发布） */
        status: number
        /** 目标范围类型（0-全部 1-部门 2-角色 3-指定用户） */
        targetType: number
        /** 目标ID列表（部门ID/角色ID/用户ID），全部时可不传 */
        targetIds?: number[]
    }
}