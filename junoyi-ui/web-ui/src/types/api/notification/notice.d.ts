declare namespace Api.Notification {

    /**
     * 我的通知VO
     */
    interface MyNotification {
        id: number
        title: string
        summary: string
        content: string
        type: number
        typeLabel: string
        typeType: string
        read: boolean
        readTime: string
        publishedAt: string
    }
}
