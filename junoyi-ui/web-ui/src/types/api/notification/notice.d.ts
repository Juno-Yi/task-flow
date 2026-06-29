declare namespace Api.Notification {

    /**
     * 我的通知列表项VO
     */
    interface MyNotification {
        id: number
        title: string
        summary: string
        type: number
        typeLabel: string
        typeType: string
        read: boolean
        readTime: string
        publishedBy: string
        publishedAt: string
    }

    /**
     * 我的通知详情VO
     */
    interface MyNotificationDetail {
        id: number
        title: string
        summary: string
        content: string
        type: number
        typeLabel: string
        typeType: string
        read: boolean
        readTime: string
        publishedBy: string
        publishedAt: string
    }

    /**
     * 用户未读通知数量
     */
    interface MyNotificationUnreadCountVO {
        count: number
    }
}
