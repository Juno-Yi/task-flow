import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'

/**
 * 获取菜单树形列表（不分页）
 */
export function fetchGetMenuTree(params?: Api.System.MenuQueryDTO) {
  return request.get<Api.System.MenuVO[]>({
    url: '/system/menu/tree',
    params
  })
}

/**
 * 获取菜单列表（分页）
 */
export function fetchGetMenuList(params?: Api.System.MenuQueryDTO) {
  return request.get<PageResult<Api.System.MenuVO>>({
    url: '/system/menu/list',
    params
  })
}

/**
 * 获取菜单详情
 */
export function fetchGetMenuById(id: number | string) {
  return request.get<Api.System.MenuVO>({
    url: `/system/menu/${id}`
  })
}

/**
 * 添加菜单
 */
export function fetchAddMenu(data: Api.System.MenuDTO) {
  return request.post<number>({
    url: '/system/menu',
    data,
    showSuccessMessage: true
  })
}

/**
 * 更新菜单
 */
export function fetchUpdateMenu(data: Api.System.MenuDTO) {
  return request.put<void>({
    url: '/system/menu',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除菜单
 */
export function fetchDeleteMenu(id: number | string) {
  return request.del<void>({
    url: `/system/menu/${id}`,
    showSuccessMessage: true
  })
}

/**
 * 批量更新菜单排序
 */
export function fetchUpdateMenuSort(data: Api.System.MenuSortDTO) {
  return request.put<void>({
    url: '/system/menu/sort',
    data,
    showSuccessMessage: true
  })
}
