/**
 * 权限池相关类型定义
 */
declare namespace Api.System {
  /**
   * 权限池查询参数
   */
  interface PermissionPoolQueryDTO {
    /** 权限标识 */
    permission?: string
    /** 权限描述 */
    description?: string
    /** 状态（0禁用 1启用） */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 权限池VO
   */
  interface PermissionPoolVO {
    /** 主键 */
    id: number
    /** 权限标识 */
    permission: string
    /** 权限描述 */
    description?: string
    /** 状态（0禁用 1启用） */
    status: number
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 权限池DTO
   */
  interface PermissionPoolDTO {
    /** 主键（修改时必填） */
    id?: number
    /** 权限标识 */
    permission: string
    /** 权限描述 */
    description?: string
    /** 状态（0禁用 1启用） */
    status?: number
  }
}
