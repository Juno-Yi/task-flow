/**
 * usePermission - 权限验证 Hook
 *
 * 提供简洁的权限检查功能，基于用户 permissions 列表验证权限
 *
 * ## 使用示例
 *
 * ```typescript
 * const { hasPermission, hasAnyPermission, hasAllPermissions } = usePermission()
 *
 * // 检查单个权限
 * if (hasPermission('system.menu.add')) {
 *   // 显示新增按钮
 * }
 *
 * // 检查是否有任一权限
 * if (hasAnyPermission(['system.menu.add', 'system.menu.edit'])) {
 *   // 显示操作区域
 * }
 *
 * // 检查是否有所有权限
 * if (hasAllPermissions(['system.menu.add', 'system.menu.edit'])) {
 *   // 显示高级操作
 * }
 *
 * // 在模板中使用
 * <el-button v-if="hasPermission('system.menu.edit')">编辑</el-button>
 * ```
 *
 * ## 权限规则
 *
 * - `*` - 超级管理员，拥有所有权限
 * - `**` - 超级管理员，拥有所有权限
 * - `system.*` - 拥有 system 模块下单级权限
 * - `system.**` - 拥有 system 模块下所有多级权限
 * - `-system.menu.delete` - 黑名单，禁止该权限（优先级最高）
 * - `-system.**` - 黑名单通配符，禁止 system 下所有权限
 *
 * @module hooks/usePermission
 * @author Fan
 */

import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'

/** 黑名单前缀 */
const DENY_PREFIX = '-'
/** 单级通配符 */
const SINGLE_WILDCARD = '*'
/** 多级通配符 */
const MULTI_WILDCARD = '**'
/** 节点分隔符 */
const SEPARATOR = '.'

/**
 * 递归匹配权限节点各部分
 */
function matchParts(
  patternParts: string[],
  patternIndex: number,
  permissionParts: string[],
  permissionIndex: number
): boolean {
  // 模式已匹配完
  if (patternIndex >= patternParts.length) {
    return permissionIndex >= permissionParts.length
  }

  const patternPart = patternParts[patternIndex]

  // 多级通配符 **
  if (patternPart === MULTI_WILDCARD) {
    // ** 在末尾，匹配剩余所有
    if (patternIndex === patternParts.length - 1) {
      return true
    }
    // ** 不在末尾，尝试匹配后续
    for (let i = permissionIndex; i <= permissionParts.length; i++) {
      if (matchParts(patternParts, patternIndex + 1, permissionParts, i)) {
        return true
      }
    }
    return false
  }

  // 权限已匹配完但模式还有
  if (permissionIndex >= permissionParts.length) {
    return false
  }

  const permissionPart = permissionParts[permissionIndex]

  // 单级通配符 * 或精确匹配
  if (patternPart === SINGLE_WILDCARD || patternPart === permissionPart) {
    return matchParts(patternParts, patternIndex + 1, permissionParts, permissionIndex + 1)
  }

  return false
}

/**
 * 通配符匹配
 *
 * 支持：
 * - `*` 匹配单级：system.user.* 匹配 system.user.delete
 * - `**` 匹配多级：system.** 匹配 system.user.delete.field
 *
 * @param pattern 权限模式（可包含通配符）
 * @param permission 待匹配的权限节点
 * @returns 是否匹配成功
 */
function matchWildcard(pattern: string, permission: string): boolean {
  if (!pattern || !permission) {
    return false
  }

  // 完全相等
  if (pattern === permission) {
    return true
  }

  // 全局通配符
  if (pattern === SINGLE_WILDCARD || pattern === MULTI_WILDCARD) {
    return true
  }

  const patternParts = pattern.split(SEPARATOR)
  const permissionParts = permission.split(SEPARATOR)

  return matchParts(patternParts, 0, permissionParts, 0)
}

/**
 * 检查用户是否拥有指定权限
 *
 * @param requiredPermission 需要的权限标识
 * @param userPermissions 用户拥有的权限列表
 * @returns 是否有权限
 */
function checkPermission(requiredPermission: string, userPermissions: string[]): boolean {
  if (!requiredPermission || !userPermissions?.length) {
    return false
  }

  // 检查精确黑名单（优先级最高）
  const denyPermission = DENY_PREFIX + requiredPermission
  if (userPermissions.includes(denyPermission)) {
    return false
  }

  // 检查通配符黑名单
  for (const permission of userPermissions) {
    if (permission.startsWith(DENY_PREFIX)) {
      const denyPattern = permission.substring(1)
      if (matchWildcard(denyPattern, requiredPermission)) {
        return false
      }
    }
  }

  // 精确匹配
  if (userPermissions.includes(requiredPermission)) {
    return true
  }

  // 全局通配符
  if (userPermissions.includes(SINGLE_WILDCARD) || userPermissions.includes(MULTI_WILDCARD)) {
    return true
  }

  // 通配符匹配
  for (const pattern of userPermissions) {
    // 跳过黑名单
    if (pattern.startsWith(DENY_PREFIX)) {
      continue
    }
    if (matchWildcard(pattern, requiredPermission)) {
      return true
    }
  }

  return false
}

/**
 * 权限验证 Hook
 */
export function usePermission() {
  const userStore = useUserStore()

  // 用户权限列表
  const permissions = computed(() => userStore.info?.permissions || [])

  /**
   * 检查是否有指定权限
   * @param permission 权限标识
   */
  const hasPermission = (permission: string): boolean => {
    return checkPermission(permission, permissions.value)
  }

  /**
   * 检查是否有任一权限（OR 逻辑）
   * @param permissionList 权限标识列表
   */
  const hasAnyPermission = (permissionList: string[]): boolean => {
    return permissionList.some((perm) => checkPermission(perm, permissions.value))
  }

  /**
   * 检查是否有所有权限（AND 逻辑）
   * @param permissionList 权限标识列表
   */
  const hasAllPermissions = (permissionList: string[]): boolean => {
    return permissionList.every((perm) => checkPermission(perm, permissions.value))
  }

  /**
   * 检查是否是超级管理员
   */
  const isSuperAdmin = computed(() => 
    permissions.value.includes(SINGLE_WILDCARD) || 
    permissions.value.includes(MULTI_WILDCARD)
  )

  /**
   * 获取黑名单权限列表（不含前缀）
   */
  const denyPermissions = computed(() => 
    permissions.value
      .filter(p => p.startsWith(DENY_PREFIX))
      .map(p => p.substring(1))
  )

  /**
   * 获取允许的权限列表（排除黑名单）
   */
  const allowPermissions = computed(() => 
    permissions.value.filter(p => !p.startsWith(DENY_PREFIX))
  )

  return {
    permissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    isSuperAdmin,
    denyPermissions,
    allowPermissions
  }
}

// 导出检查函数供指令使用
export { checkPermission, matchWildcard }
