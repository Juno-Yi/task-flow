/**
 * 系统用户相关类型定义
 */
declare namespace Api.System {
  /**
   * 用户查询参数
   */
  interface UserQueryDTO {
    /** 部门ID */
    deptId?: number
    /** 用户名 */
    userName?: string
    /** 昵称 */
    nickName?: string
    /** 邮箱 */
    email?: string
    /** 手机号 */
    phonenumber?: string
    /** 性别 */
    sex?: string
    /** 状态 */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 用户视图对象
   */
  interface SysUserVO {
    /** 用户ID */
    userId: number
    /** 部门ID */
    deptId?: number
    /** 部门名称 */
    deptName?: string
    /** 用户名 */
    userName: string
    /** 昵称 */
    nickName?: string
    /** 头像 */
    avatar?: string
    /** 邮箱（可能脱敏） */
    email?: string
    /** 手机号（可能脱敏） */
    phonenumber?: string
    /** 性别 */
    sex?: string
    /** 性别标签（字典翻译） */
    sexLabel?: string
    /** 性别标签类型（用于前端标签颜色） */
    sexType?: string
    /** 状态 1-启用 0-禁用 */
    status: number
    /** 状态标签（字典翻译） */
    statusLabel?: string
    /** 状态标签类型（用于前端标签颜色） */
    statusType?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
    /** 备注 */
    remark?: string
  }

  /**
   * 用户传输数据
   */
  interface SysUserDTO {
    /** 用户ID（修改时必填） */
    id?: number
    /** 用户名 */
    userName: string
    /** 密码（添加时必填） */
    password?: string
    /** 昵称 */
    nickName?: string
    /** 手机号 */
    phonenumber?: string
    /** 邮箱 */
    email?: string
    /** 性别（0-男，1-女） */
    sex?: string
    /** 状态（1-启用，0-禁用） */
    status?: number
    /** 备注 */
    remark?: string
  }

  /**
   * 用户独立权限VO
   */
  interface SysUserPermVO {
    /** 权限ID */
    id: number
    /** 权限标识 */
    permission: string
    /** 创建时间 */
    createTime?: string
  }

  /**
   * 用户个人信息更新DTO
   */
  interface UserProfileDTO {
    /** 昵称 */
    nickName?: string
    /** 手机号 */
    phonenumber?: string
    /** 邮箱 */
    email?: string
    /** 性别（0-男，1-女） */
    sex?: string
    /** 头像URL */
    avatar?: string
  }

  /**
   * 修改密码DTO
   */
  interface ChangePasswordDTO {
    /** 当前密码 */
    oldPassword: string
    /** 新密码 */
    newPassword: string
    /** 确认新密码 */
    confirmPassword: string
  }
}
