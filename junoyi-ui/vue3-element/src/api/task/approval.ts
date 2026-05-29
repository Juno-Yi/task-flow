import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取任务审核列表（分页）
 */
export function fetchGetTaskApprovalList(params?: Api.Task.TaskListQueryDTO) {
    return request.get<PageResult<Api.Task.TaskListVO>>({
        url: '/task/approval/list',
        params
    })
}

/**
 * 审核通过任务
 */
export function fetchPassTaskApproval(data: Api.Task.TaskApprovalDTO) {
    return request.post<void>({
        url: '/task/approval/pass',
        data
    })
}

/**
 * 驳回任务
 */
export function fetchRejectTaskApproval(data: Api.Task.TaskApprovalDTO) {
    return request.post<void>({
        url: '/task/approval/reject',
        data
    })
}
