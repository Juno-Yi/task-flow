/**
 * useAuth - 权限验证管理
 *
 * 提供统一的权限验证功能，基于后端路由 meta.authList 配置检查权限
 *
 * ## 使用示例
 *
 * ```typescript
 * const { hasAuth } = useAuth()
 *
 * // 检查是否有新增权限
 * if (hasAuth('add')) {
 *   // 显示新增按钮
 * }
 *
 * // 在模板中使用
 * <el-button v-if="hasAuth('edit')">编辑</el-button>
 * <el-button v-if="hasAuth('delete')">删除</el-button>
 * ```
 *
 * @module useAuth
 * @author JunoYi Team
 */

import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/store/modules/user'
import type { AppRouteRecord } from '@/types/router'

type AuthItem = NonNullable<AppRouteRecord['meta']['authList']>[number]

const userStore = useUserStore()

export const useAuth = () => {
  const route = useRoute()
  const { info } = storeToRefs(userStore)

  // 用户权限列表（如 ['*', 'user:add', 'user:edit']）
  const userPermissions = info.value?.permissions ?? []

  // 路由 meta 配置的权限列表（如 [{ authMark: 'add' }]）
  const routeAuthList: AuthItem[] = Array.isArray(route.meta.authList)
    ? (route.meta.authList as AuthItem[])
    : []

  /**
   * 检查是否拥有某权限标识
   * @param auth 权限标识
   * @returns 是否有权限
   */
  const hasAuth = (auth: string): boolean => {
    // 超级管理员（拥有 * 权限）拥有所有权限
    if (userPermissions.includes('*')) {
      return true
    }

    // 检查用户权限列表
    if (userPermissions.includes(auth)) {
      return true
    }

    // 检查路由 meta.authList 配置
    return routeAuthList.some((item) => item?.authMark === auth)
  }

  return {
    hasAuth
  }
}
