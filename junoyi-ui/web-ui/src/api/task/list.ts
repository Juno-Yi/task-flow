import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取任务列表（分页）
 */
export function fetchGetTaskList(params?: Api.Task.TaskListQueryDTO) {
  return request.get<PageResult<Api.Task.TaskListVO>>({
    url: '/task/list/list',
    params
  })
}

/**
 * 获取任务详情
 */
export function fetchGetTaskDetail(taskId: number) {
  return request.get<Api.Task.TaskListDetailVO>({
    url: `/task/list/${taskId}`
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

/**
 * 新增任务
 */
export function fetchAddTask(data: Api.Task.TaskListDTO) {
  return request.post<void>({
    url: '/task/list',
    data
  })
}

/**
 * 修改任务
 */
export function fetchUpdateTask(data: Api.Task.TaskListDTO) {
  return request.put<void>({
    url: '/task/list',
    data
  })
}

/**
 * 获取任务日志列表（分页）
 */
export function fetchGetTaskLogList(params?: Api.Task.TaskLogQueryDTO) {
  return request.get<PageResult<Api.Task.TaskLogVO>>({
    url: '/task/log/list',
    params
  })
}

/**
 * 催办任务
 */
export function fetchRemindTask(taskId: number) {
  return request.post<void>({
    url: `/task/list/remind/${taskId}`
  })
}

