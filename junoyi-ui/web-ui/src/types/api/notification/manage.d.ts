declare namespace Api.Notification {

    /**
     * 通知列表 VO
     */
    interface NotificationListVO {
        id: number
        title: string
        content: string
        type: number
        typeLabel; string
        typeType: string
        status: number
        statusLabel: string
        statusType: string
        senderId: number
        senderNickName: string
        publishTime: string
        updateTime: string
    }
}