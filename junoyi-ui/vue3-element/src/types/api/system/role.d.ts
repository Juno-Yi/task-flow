/**
 * 系统角色相关类型定义
 */
declare namespace Api.System {
  /**
   * 角色查询参数
   */
  interface RoleQueryDTO {
    /** 角色名称 */
    roleName?: string
    /** 角色标识 */
    roleKey?: string
    /** 状态 */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 角色VO类型（后端返回格式）
   */
  interface RoleVO {
    /** 角色ID */
    id: number
    /** 角色名称 */
    roleName: string
    /** 角色标识 */
    roleKey: string
    /** 排序 */
    sort: number
    /** 数据范围 */
    dataScope: string
    /** 数据范围标签（字典翻译） */
    dataScopeLabel?: string
    /** 数据范围标签类型（用于前端标签颜色） */
    dataScopeType?: string
    /** 状态（0禁用 1启用） */
    status: number
    /** 状态标签（字典翻译） */
    statusLabel?: string
    /** 状态标签类型（用于前端标签颜色） */
    statusType?: string
    /** 创建时间 */
    createTime: string
    /** 更新时间 */
    updateTime: string
    /** 备注 */
    remark: string
  }

  /**
   * 角色DTO类型（请求参数）
   */
  interface RoleDTO {
    /** 角色ID */
    id?: number
    /** 角色名称 */
    roleName?: string
    /** 角色标识 */
    roleKey?: string
    /** 排序 */
    sort?: number
    /** 数据范围 */
    dataScope?: string
    /** 状态（0禁用 1启用） */
    status?: number
    /** 备注 */
    remark?: string
    /** 菜单ID列表 */
    menuIds?: number[]
  }
}
