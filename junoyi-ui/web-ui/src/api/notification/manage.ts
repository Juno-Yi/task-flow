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