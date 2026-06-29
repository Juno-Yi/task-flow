import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取通知发布日志列表
 * @param params 查询参数
 */
export function fetchGetNotificationPublishLogList(params: Api.Notification.NotificationPublishLogQueryDTO){
    return request.get<PageResult<Api.Notification.NotificationPublishLogVO>>({
        url: '/notification/publish-log/list',
        params
    })
}