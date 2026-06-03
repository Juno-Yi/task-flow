import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取会话列表（分页）
 */
export function fetchGetSessionList(params?: Api.System.SessionQueryDTO) {
  return request.get<PageResult<Api.System.SessionVO>>({
    url: '/system/session/list',
    params
  })
}

/**
 * 强制下线（单个）
 */
export function fetchForceLogout(sessionId: string) {
  return request.del<void>({
    url: `/system/session/${sessionId}`,
    showSuccessMessage: true
  })
}

/**
 * 批量踢出会话
 */
export function fetchBatchKickOut(sessionIds: string[]) {
  return request.del<void>({
    url: '/system/session/batch',
    data: sessionIds,
    showSuccessMessage: true
  })
}
