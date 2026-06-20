import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取通知列表
 */
export function fetchGetNotificationList(){
    return request.get<PageResult<Api.Notification.NotificationListVO>>({
        url: '/notification/list'
    })
}

/**
 * 添加通知（立即发布或存储草稿）
 */
export function fetchAddNotification(data: Api.Notification.NotificationDTO) {
    return request.post<void>({
        url: '/notification',
        data
    })
}