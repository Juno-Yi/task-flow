import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取权限池列表（分页）
 */
export function fetchGetPermissionPoolList(params?: Api.System.PermissionPoolQueryDTO) {
  return request.get<PageResult<Api.System.PermissionPoolVO>>({
    url: '/system/permission-pool/list',
    params
  })
}

/**
 * 获取权限池下拉选项
 */
export function fetchGetPermissionPoolOptions() {
  return request.get<Api.System.PermissionPoolVO[]>({
    url: '/system/permission-pool/options'
  })
}

/**
 * 添加权限
 */
export function fetchAddPermissionPool(data: Api.System.PermissionPoolDTO) {
  return request.post<void>({
    url: '/system/permission-pool',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除权限
 */
export function fetchDeletePermissionPool(id: number) {
  return request.del<void>({
    url: `/system/permission-pool/${id}`,
    showSuccessMessage: true
  })
}

/**
 * 批量删除权限
 */
export function fetchDeletePermissionPoolBatch(ids: number[]) {
  return request.del<void>({
    url: '/system/permission-pool/batch',
    data: ids,
    showSuccessMessage: true
  })
}

/**
 * 更新权限状态
 */
export function fetchUpdatePermissionPoolStatus(id: number, status: number) {
  return request.put<void>({
    url: `/system/permission-pool/${id}/status`,
    data: { status },
    showSuccessMessage: true
  })
}
