/**
 * v-roles 角色权限指令
 *
 * 基于用户角色控制 DOM 元素的显示和隐藏。
 * 只要用户拥有指定角色中的任意一个，元素就会显示，否则从 DOM 中移除。
 * 超级管理员（角色ID=1）拥有所有权限。
 *
 * ## 主要功能
 *
 * - 角色验证 - 检查用户是否拥有指定角色
 * - 多角色支持 - 支持单个角色或多个角色（满足其一即可）
 * - DOM 控制 - 无权限时自动移除元素，而非隐藏
 * - 响应式更新 - 角色变化时自动更新元素状态
 *
 * ## 使用示例
 *
 * ```vue
 * <template>
 *   <!-- 单个角色 - 只有角色ID为1的用户可见 -->
 *   <el-button v-roles="1">超级管理员功能</el-button>
 *
 *   <!-- 多个角色 - 角色ID为1或2的用户可见 -->
 *   <el-button v-roles="[1, 2]">管理员功能</el-button>
 * </template>
 * ```
 *
 * ## 权限逻辑
 *
 * - 用户角色从 userStore.getUserInfo.roles 获取
 * - 超级管理员（角色ID=1）拥有所有权限
 * - 只要用户拥有指定角色中的任意一个，元素就会显示
 * - 如果用户没有任何角色或不满足条件，元素将被移除
 *
 * @module directives/roles
 * @author Art Design Pro Team
 */

import { useUserStore } from '@/store/modules/user'
import { App, Directive, DirectiveBinding } from 'vue'

interface RolesBinding extends DirectiveBinding {
  value: number | number[]
}

function checkRolePermission(el: HTMLElement, binding: RolesBinding): void {
  const userStore = useUserStore()
  const userRoles = userStore.getUserInfo.roles

  // 如果用户角色为空或未定义，移除元素
  if (!userRoles?.length) {
    removeElement(el)
    return
  }

  // 超级管理员（角色ID=1）拥有所有权限
  if (userRoles.includes(1)) {
    return
  }

  // 确保指令值为数组格式
  const requiredRoles = Array.isArray(binding.value) ? binding.value : [binding.value]

  // 检查用户是否具有所需角色之一
  const hasPermission = requiredRoles.some((role: number) => userRoles.includes(role))

  // 如果没有权限，安全地移除元素
  if (!hasPermission) {
    removeElement(el)
  }
}

function removeElement(el: HTMLElement): void {
  if (el.parentNode) {
    el.parentNode.removeChild(el)
  }
}

const rolesDirective: Directive = {
  mounted: checkRolePermission,
  updated: checkRolePermission
}

export function setupRolesDirective(app: App): void {
  app.directive('roles', rolesDirective)
}
