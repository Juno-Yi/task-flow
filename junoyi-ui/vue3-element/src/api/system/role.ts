import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取角色列表（分页）
 */
export function fetchGetRoleList(params?: Api.System.RoleQueryDTO) {
  return request.get<PageResult<Api.System.RoleVO>>({
    url: '/system/role/list',
    params
  })
}

/**
 * 获取角色下拉列表选项
 */
export function fetchGetRoleOptions() {
  return request.get<Api.System.RoleVO[]>({
    url: '/system/role/options'
  })
}

/**
 * 获取角色详情
 */
export function fetchGetRoleById(id: number | string) {
  return request.get<Api.System.RoleVO>({
    url: `/system/role/${id}`
  })
}

/**
 * 添加角色
 */
export function fetchAddRole(data: Api.System.RoleDTO) {
  return request.post<void>({
    url: '/system/role',
    data,
    showSuccessMessage: true
  })
}

/**
 * 更新角色
 */
export function fetchUpdateRole(data: Api.System.RoleDTO) {
  return request.put<void>({
    url: '/system/role',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除角色
 */
export function fetchDeleteRole(id: number | string) {
  return request.del<void>({
    url: `/system/role/${id}`,
    showSuccessMessage: true
  })
}

/**
 * 批量删除角色
 */
export function fetchDeleteRoleBatch(ids: number[]) {
  return request.del<void>({
    url: '/system/role/batch',
    data: ids,
    showSuccessMessage: true
  })
}

/**
 * 获取角色已绑定的权限组列表
 */
export function fetchGetRolePermissionGroups(roleId: number) {
  return request.get<Api.System.PermissionGroupVO[]>({
    url: `/system/role/${roleId}/permission-groups`
  })
}

/**
 * 更新角色权限组绑定
 */
export function fetchUpdateRolePermissionGroups(roleId: number, groupIds: number[]) {
  return request.put<void>({
    url: `/system/role/${roleId}/permission-groups`,
    data: groupIds,
  })
}
