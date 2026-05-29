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