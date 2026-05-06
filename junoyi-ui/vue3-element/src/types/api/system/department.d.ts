/**
 * 系统部门相关类型定义
 */
declare namespace Api.System {
  /**
   * 部门查询参数
   */
  interface DeptQueryDTO {
    /** 部门名称 */
    name?: string
    /** 负责人 */
    leader?: string
    /** 联系电话 */
    phonenumber?: string
    /** 邮箱 */
    email?: string
    /** 状态 */
    status?: number
  }

  /**
   * 部门VO类型（后端返回格式）
   */
  interface DeptVO {
    /** 主键 */
    id: number
    /** 父部门ID */
    parentId: number
    /** 部门名称 */
    name: string
    /** 排序号 */
    sort: number
    /** 负责人 */
    leader: string
    /** 联系电话 */
    phonenumber: string
    /** 邮箱 */
    email: string
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
    /** 子部门列表 */
    children?: DeptVO[]
  }

  /**
   * 部门DTO类型（请求参数）
   */
  interface DeptDTO {
    /** 主键 */
    id?: number
    /** 父部门ID */
    parentId?: number
    /** 部门名称 */
    name?: string
    /** 负责人 */
    leader?: string
    /** 联系电话 */
    phonenumber?: string
    /** 邮箱 */
    email?: string
    /** 排序号 */
    sort?: number
    /** 状态（0禁用 1启用） */
    status?: number
    /** 备注 */
    remark?: string
  }

  /**
   * 部门排序项
   */
  interface DeptSortItem {
    /** 部门ID */
    id: number
    /** 父部门ID */
    parentId: number
    /** 排序值 */
    sort: number
  }

  /**
   * 部门排序DTO
   */
  interface DeptSortDTO {
    /** 排序项列表 */
    items: DeptSortItem[]
  }
}
