/**
 * API 接口类型定义模块
 *
 * 提供所有后端接口的类型定义
 *
 * ## 主要功能
 *
 * - 通用类型（分页参数、响应结构等）
 * - 认证类型（登录、用户信息等）
 * - 系统管理类型（用户、角色等）
 * - 全局命名空间声明
 *
 * ## 使用场景
 *
 * - API 请求参数类型约束
 * - API 响应数据类型定义
 * - 接口文档类型同步
 *
 * ## 注意事项
 *
 * - 在 .vue 文件使用需要在 eslint.config.mjs 中配置 globals: { Api: 'readonly' }
 * - 使用全局命名空间，无需导入即可使用
 *
 * ## 使用方式
 *
 * ```typescript
 * const params: Api.Auth.LoginParams = { userName: 'admin', password: '123456' }
 * const response: Api.Auth.UserInfo = await fetchUserInfo()
 * ```
 *
 * @module types/api/api
 * @author Art Design Pro Team
 */

declare namespace Api {
  /** 通用类型 */
  namespace Common {
    /** 分页参数 */
    interface PaginationParams {
      /** 当前页码 */
      current: number
      /** 每页条数 */
      size: number
      /** 总条数 */
      total: number
    }

    /** 通用搜索参数 */
    type CommonSearchParams = Pick<PaginationParams, 'current' | 'size'>

    /** 分页响应基础结构 */
    interface PaginatedResponse<T = any> {
      records: T[]
      current: number
      size: number
      total: number
    }

    /** 启用状态 */
    type EnableStatus = '1' | '2'

    /** 文件信息 */
    interface FileInfo {
      /** 文件ID */
      fileId?: number
      /** 原始文件名 */
      originalName: string
      /** 存储文件名 */
      storageName?: string
      /** 文件路径 */
      filePath?: string
      /** 文件URL（访问地址） */
      fileUrl: string
      /** 文件大小（字节） */
      fileSize?: number
      /** 文件类型（MIME类型） */
      contentType?: string
      /** 文件扩展名 */
      extension?: string
      /** 存储类型 */
      storageType?: string
    }
  }

  /** 认证类型 */
  namespace Auth {
    /** 登录平台类型 */
    type PlatformType = 'ADMIN_WEB' | 'FRONT_DESK_WEB' | 'MINI_PROGRAM' | 'APP' | 'DESKTOP_APP'

    /** 登录参数 */
    interface LoginParams {
      /** 验证码ID */
      captchaId: string
      /** 用户名 */
      username?: string
      /** 邮箱 */
      email?: string
      /** 手机号 */
      phonenumber?: string
      /** 密码 */
      password: string
      /** 验证码 */
      code: string
      /** 登录平台类型 */
      platformType?: PlatformType
    }

    /** 登录响应 */
    interface LoginResponse {
      accessToken: string
      refreshToken: string
    }

    /** 验证码配置响应 */
    interface CaptchaConfigResponse {
      /** 是否启用验证码 */
      enabled: boolean
    }

    /** 验证码响应 */
    interface CaptchaResponse {
      captchaId: string
      type: string
      image: string
      backgroundImage: string | null
      sliderImage: string | null
      backgroundWidth: number | null
      backgroundHeight: number | null
      originalImage: string | null
      wordList: string[] | null
      expireSeconds: number
    }

    /** 用户信息 */
    interface UserInfo {
      /** 用户ID */
      userId: number
      /** 用户名 */
      userName: string
      /** 用户昵称 */
      nickName: string
      /** 邮箱 */
      email: string
      /** 头像 */
      avatar: string
      /** 权限列表 */
      permissions: string[]
      /** 角色ID列表 */
      roles: number[]
      /** 部门ID列表 */
      depts: number[]
    }
  }

  /** 系统管理类型 */
  namespace SystemManage {
    /** 用户列表 */
    type UserList = Api.Common.PaginatedResponse<UserListItem>

    /** 用户列表项 */
    interface UserListItem {
      id: number
      avatar: string
      status: string
      userName: string
      userGender: string
      nickName: string
      userPhone: string
      userEmail: string
      userRoles: string[]
      createBy: string
      createTime: string
      updateBy: string
      updateTime: string
    }

    /** 用户搜索参数 */
    type UserSearchParams = Partial<
      Pick<UserListItem, 'id' | 'userName' | 'userGender' | 'userPhone' | 'userEmail' | 'status'> &
        Api.Common.CommonSearchParams
    >

    /** 角色列表 */
    type RoleList = Api.Common.PaginatedResponse<RoleListItem>

    /** 角色列表项 */
    interface RoleListItem {
      roleId: number
      roleName: string
      roleCode: string
      description: string
      enabled: boolean
      createTime: string
    }

    /** 角色搜索参数 */
    type RoleSearchParams = Partial<
      Pick<RoleListItem, 'roleId' | 'roleName' | 'roleCode' | 'description' | 'enabled'> &
        Api.Common.CommonSearchParams
    >
  }
}
