/**
 * 项目角色 Hook
 * 用于判断当前用户在项目中的角色
 */

import { computed, type ComputedRef, type Ref } from 'vue'
import { ProjectRole } from '@/enums/project'
import { useUserStore } from '@/store/modules/user'

export interface ProjectRoleInfo {
  // 角色信息
  role: ComputedRef<string | null | undefined>
  isOwner: ComputedRef<boolean>
  isAdmin: ComputedRef<boolean>
  isMember: ComputedRef<boolean>
  isViewer: ComputedRef<boolean>
  isProjectMember: ComputedRef<boolean>
  isSuperAdmin: ComputedRef<boolean>
}

/**
 * 使用项目角色
 * @param role 当前用户在项目中的角色
 */
export function useProjectRole(role: ComputedRef<string | null | undefined> | Ref<string | null | undefined> | string | null | undefined): ProjectRoleInfo {
  const userStore = useUserStore()
  
  const currentRole = computed(() => {
    if (typeof role === 'string' || role === null || role === undefined) {
      return role
    }
    return role.value
  })

  // 超级管理员判断（用户ID为1）
  const isSuperAdmin = computed(() => userStore.getUserInfo?.userId === 1)

  // 角色判断（超级管理员拥有所有角色权限）
  const isOwner = computed(() => isSuperAdmin.value || currentRole.value === ProjectRole.OWNER)
  const isAdmin = computed(() => isSuperAdmin.value || currentRole.value === ProjectRole.ADMIN)
  const isMember = computed(() => isSuperAdmin.value || currentRole.value === ProjectRole.MEMBER)
  const isViewer = computed(() => isSuperAdmin.value || currentRole.value === ProjectRole.VIEWER)
  const isProjectMember = computed(() => isSuperAdmin.value || (currentRole.value !== null && currentRole.value !== undefined))

  return {
    role: currentRole,
    isOwner,
    isAdmin,
    isMember,
    isViewer,
    isProjectMember,
    isSuperAdmin
  }
}
