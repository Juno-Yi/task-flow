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
 * 获取通知下拉列表
 */
export function fetchGetNotificationOptions(){
    return request.get<Api.Notification.NotificationOptionVO[]>({
        url: '/notification/options'
    })
}

/**
 * 获取通知详情
 */
export function fetchGetNotificationById(id: number) {
    return request.get<Api.Notification.NotificationDetailVO>({
        url: `/notification/${id}`
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

/**
 * 修改通知
 */
export function  fetchUpdateNotification(data: Api.Notification.NotificationDTO){
    return request.put<void>({
        url: '/notification',
        data
    })
}

/**
 * 发布通知（将草稿改为已发布）
 */
export function fetchPublishNotification(notificationId: number) {
    return request.post<void>({
        url: `/notification/${notificationId}/publish`
    })
}