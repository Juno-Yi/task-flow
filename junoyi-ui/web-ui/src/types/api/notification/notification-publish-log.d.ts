declare namespace Api.Notification {

    /**
     * 通知发布日志 VO
     */
    interface NotificationPublishLogVO {
        id: number
        notificationId: number
        notificationTitle: string
        notificationSummary: string
        publishUserId: number
        publishUserNickName: string
        publishTime: string
    }

    /**
     * 通知发布日志查询 DTO
     */
    interface NotificationPublishLogQueryDTO{
        notificationId?: number
        publishUserId?: number
        startTime?: string
        endTime?: string
    }

    /**
     * 通知选项 VO（用于下拉选择）
     */
    interface NotificationOptionVO {
        /** 通知ID */
        id: number
        /** 通知标题 */
        title: string
    }
}