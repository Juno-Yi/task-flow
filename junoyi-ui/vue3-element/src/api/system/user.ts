import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取用户列表（分页）
 */
export function fetchGetUserList(params: Api.System.UserQueryDTO) {
  return request.get<PageResult<Api.System.SysUserVO>>({
    url: '/system/user/list',
    params
  })
}

/**
 * 添加用户
 */
export function fetchAddUser(data: Api.System.SysUserDTO) {
  return request.post<void>({
    url: '/system/user',
    data,
    showSuccessMessage: true
  })
}

/**
 * 更新用户
 */
export function fetchUpdateUser(data: Api.System.SysUserDTO) {
  return request.put<void>({
    url: '/system/user',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除用户
 */
export function fetchDeleteUser(id: number) {
  return request.del<void>({
    url: `/system/user/${id}`,
    showSuccessMessage: true
  })
}

/**
 * 批量删除用户
 */
export function fetchDeleteUserBatch(ids: number[]) {
  return request.del<void>({
    url: '/system/user/batch',
    data: ids,
    showSuccessMessage: true
  })
}

/**
 * 获取用户已绑定的角色列表
 */
export function fetchGetUserRoles(userId: number) {
  return request.get<Api.System.RoleVO[]>({
    url: `/system/user/${userId}/roles`
  })
}

/**
 * 更新用户角色绑定
 */
export function fetchUpdateUserRoles(userId: number, roleIds: number[]) {
  return request.put<void>({
    url: `/system/user/${userId}/roles`,
    data: roleIds,
  })
}

/**
 * 获取用户已绑定的部门列表
 */
export function fetchGetUserDepts(userId: number) {
  return request.get<Api.System.DeptVO[]>({
    url: `/system/user/${userId}/depts`
  })
}

/**
 * 更新用户部门绑定
 */
export function fetchUpdateUserDepts(userId: number, deptIds: number[]) {
  return request.put<void>({
    url: `/system/user/${userId}/depts`,
    data: deptIds,
  })
}

/**
 * 重置用户密码
 */
export function fetchResetUserPassword(data: { userId: number; newPassword: string }) {
  return request.put<void>({
    url: `/system/user/${data.userId}/password`,
    data: {
      newPassword: data.newPassword
    },
  })
}

/**
 * 获取用户已绑定的权限组列表
 */
export function fetchGetUserPermissionGroups(userId: number) {
  return request.get<Api.System.PermissionGroupVO[]>({
    url: `/system/user/${userId}/permission-groups`
  })
}

/**
 * 更新用户权限组绑定
 */
export function fetchUpdateUserPermissionGroups(userId: number, groupIds: number[]) {
  return request.put<void>({
    url: `/system/user/${userId}/permission-groups`,
    data: groupIds,
  })
}

/**
 * 获取用户独立权限列表
 */
export function fetchGetUserPermissions(userId: number) {
  return request.get<Api.System.SysUserPermVO[]>({
    url: `/system/user/${userId}/permissions`
  })
}

/**
 * 添加用户独立权限（增量添加，已存在的不会重复）
 */
export function fetchAddUserPermissions(userId: number, permissions: string[]) {
  return request.post<void>({
    url: `/system/user/${userId}/permissions`,
    data: permissions
  })
}

/**
 * 删除用户单个独立权限
 */
export function fetchDeleteUserPermission(userId: number, permId: number) {
  return request.del<void>({
    url: `/system/user/${userId}/permissions/${permId}`
  })
}
