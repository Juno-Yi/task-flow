import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

type OperationLogVO = Api.System.SysOperLogVO
type OperationLogQueryDTO = Api.System.SysOperLogQueryDTO

/**
 * 获取操作日志列表
 */
export function fetchGetOperLogList(params?: OperationLogQueryDTO) {
    return request.get<PageResult<OperationLogVO>>({
        url: '/system/oper-log/list',
        params
    })
}

/**
 * 删除操作日志
 */
export function fetchDeleteOperLog(ids: number[]) {
    return request.del<void>({
        url: `/system/oper-log/${ids.join(',')}`,
        showSuccessMessage: true
    })
}

/**
 * 清空操作日志
 */
export function fetchClearOperLog() {
    return request.del<void>({
        url: '/system/oper-log/clear',
        showSuccessMessage: true
    })
}
