/**
 * 项目角色枚举
 */
export enum ProjectRole {
  OWNER = 'owner',
  ADMIN = 'admin',
  MEMBER = 'member',
  VIEWER = 'viewer'
}

/**
 * 项目角色名称映射
 */
export const ProjectRoleNameMap: Record<ProjectRole, string> = {
  [ProjectRole.OWNER]: '项目负责人',
  [ProjectRole.ADMIN]: '管理员',
  [ProjectRole.MEMBER]: '成员',
  [ProjectRole.VIEWER]: '只读成员'
}

/**
 * 项目角色标签类型映射
 */
export const ProjectRoleTagTypeMap: Record<ProjectRole, 'danger' | 'warning' | 'success' | 'info'> = {
  [ProjectRole.OWNER]: 'danger',
  [ProjectRole.ADMIN]: 'warning',
  [ProjectRole.MEMBER]: 'success',
  [ProjectRole.VIEWER]: 'info'
}

/**
 * 获取角色名称
 */
export const getProjectRoleName = (role: string): string => {
  return ProjectRoleNameMap[role as ProjectRole] || role
}

/**
 * 获取角色标签类型
 */
export const getProjectRoleTagType = (role: string) => {
  return ProjectRoleTagTypeMap[role as ProjectRole] || 'info'
}

/**
 * 检查是否有管理权限
 */
export const canManageProject = (role: string): boolean => {
  return role === ProjectRole.OWNER || role === ProjectRole.ADMIN
}

/**
 * 检查是否有写权限
 */
export const canWriteProject = (role: string): boolean => {
  return role !== ProjectRole.VIEWER
}

/**
 * 检查是否是项目负责人
 */
export const isProjectOwner = (role: string): boolean => {
  return role === ProjectRole.OWNER
}
