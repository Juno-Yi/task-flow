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

/**
 * 获取我的通知详情（同时标记为已读）
 */
export function fetchGetMyNotificationDetail(id: number) {
    return request.get<Api.Notification.MyNotificationDetail>({
        url: `/notification/my-notification/detail/${id}`
    })
}

/**
 * 全部标记为已读
 */
export function fetchMarkAllAsRead() {
    return request.post<number>({
        url: '/notification/my-notification/mark-all-read'
    })
}

/**
 * 获取用户未读通知数量
 */
export function fetchMyNotificationUnreadCount(){
    return request.get<Api.Notification.MyNotificationUnreadCountVO>({
        url: '/notification/my-notification/unread-count'
    })
}
