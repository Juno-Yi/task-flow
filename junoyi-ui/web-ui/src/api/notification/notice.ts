import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取我的通知列表
 */
export function fetchGetMyNotificationList(pageNum = 1, pageSize = 100) {
    return request.get<PageResult<Api.Notification.MyNotification>>({
        url: '/notification/my-notification/list',
        params: {
            pageNum,
            pageSize
        }
    })
}
