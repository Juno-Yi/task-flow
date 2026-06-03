/**
 * v-permission 权限指令
 *
 * 基于用户权限标识控制 DOM 元素的显示和隐藏。
 *
 * ## 使用示例
 *
 * ```vue
 * <el-button v-permission="'system.menu.add'">新增</el-button>
 * <el-button v-permission="'system.menu.edit'">编辑</el-button>
 * <div v-permission="['system.admin', 'system.manager']">管理员或经理可见</div>
 * ```
 *
 * ## 权限规则
 *
 * 用户 permissions 数组示例：
 * - `['*']` - 超级管理员，拥有所有权限
 * - `['system.*']` - 拥有 system 模块下所有权限
 * - `['system.menu.*']` - 拥有 system.menu 下所有权限
 * - `['system.menu.add', 'system.menu.edit']` - 拥有指定权限
 * - `['-system.menu.delete']` - 黑名单，禁止 delete 权限（优先级最高）
 *
 * @module directives/permission
 * @author Fan
 */

import { useUserStore } from '@/store/modules/user'
import { checkPermission } from '@/hooks/core/usePermission'
import { App, Directive, DirectiveBinding } from 'vue'

interface PermissionBinding extends DirectiveBinding {
  value: string | string[]
}

/**
 * 检查权限并控制元素显示
 */
function checkElementPermission(el: HTMLElement, binding: PermissionBinding): void {
  const userStore = useUserStore()
  const userPermissions = userStore.info?.permissions || []

  // 支持单个权限或权限数组
  const requiredPermissions = Array.isArray(binding.value) ? binding.value : [binding.value]

  // 检查是否拥有任一权限（OR 逻辑）
  const hasAnyPermission = requiredPermissions.some((perm) =>
    checkPermission(perm, userPermissions)
  )

  // 如果没有权限，移除元素
  if (!hasAnyPermission && el.parentNode) {
    el.parentNode.removeChild(el)
  }
}

const permissionDirective: Directive = {
  mounted: checkElementPermission,
  updated: checkElementPermission
}

/**
 * 注册 v-permission 指令
 */
export function setupPermissionDirective(app: App): void {
  app.directive('permission', permissionDirective)
}
