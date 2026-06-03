import request from '@/utils/http'

/**
 * 获取部门树形列表
 */
export function fetchGetDeptTree(params?: Api.System.DeptQueryDTO) {
  return request.get<Api.System.DeptVO[]>({
    url: '/system/dept/tree',
    params
  })
}

/**
 * 获取部门详情
 */
export function fetchGetDeptById(id: number | string) {
  return request.get<Api.System.DeptVO>({
    url: `/system/dept/${id}`
  })
}

/**
 * 添加部门
 */
export function fetchAddDept(data: Api.System.DeptDTO) {
  return request.post<number>({
    url: '/system/dept',
    data,
    showSuccessMessage: true
  })
}

/**
 * 更新部门
 */
export function fetchUpdateDept(data: Api.System.DeptDTO) {
  return request.put<void>({
    url: '/system/dept',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除部门
 */
export function fetchDeleteDept(id: number | string) {
  return request.del<void>({
    url: `/system/dept/${id}`,
    showSuccessMessage: true
  })
}

/**
 * 更新部门排序
 */
export function fetchUpdateDeptSort(data: Api.System.DeptSortDTO) {
  return request.put<void>({
    url: '/system/dept/sort',
    data,
    showSuccessMessage: true
  })
}

/**
 * 获取部门已绑定的权限组列表
 */
export function fetchGetDeptPermissionGroups(deptId: number) {
  return request.get<Api.System.PermissionGroupVO[]>({
    url: `/system/dept/${deptId}/permission-groups`
  })
}

/**
 * 更新部门权限组绑定
 */
export function fetchUpdateDeptPermissionGroups(deptId: number, groupIds: number[]) {
  return request.put<void>({
    url: `/system/dept/${deptId}/permission-groups`,
    data: groupIds,
  })
}
