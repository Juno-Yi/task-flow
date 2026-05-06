/**
 * 权限组相关类型定义
 */
declare namespace Api.System {

  /**
   * 权限组查询参数
   */
  interface PermissionGroupQueryDTO {
    /** 权限组名称 */
    groupName?: string
    /** 权限组编码 */
    groupCode?: string
    /** 状态 */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 权限组VO
   */
  interface PermissionGroupVO {
    /** 主键 */
    id: number
    /** 权限组编码 */
    groupCode: string
    /** 权限组名称 */
    groupName: string
    /** 优先级 */
    priority?: number
    /** 权限列表 */
    permissions: string[]
    /** 描述 */
    description?: string
    /** 状态 1正常 0禁用 */
    status: number
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 权限组DTO
   */
  interface PermissionGroupDTO {
    /** 主键（修改时必填） */
    id?: number
    /** 权限组编码 */
    groupCode: string
    /** 权限组名称 */
    groupName: string
    /** 优先级 */
    priority?: number
    /** 权限列表 */
    permissions: string[]
    /** 描述 */
    description?: string
    /** 状态 1正常 0禁用 */
    status?: number
  }
}
