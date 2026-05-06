/**
 * 系统菜单相关类型定义
 */
declare namespace Api.System {
  /**
   * 菜单查询参数
   */
  interface MenuQueryDTO {
    /** 菜单名称 */
    name?: string
    /** 状态 */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 菜单VO类型（后端返回格式）
   */
  interface MenuVO {
    /** 主键 */
    id: number
    /** 父菜单ID */
    parentId: number
    /** 路由名称 */
    name: string
    /** 路由路径 */
    path: string
    /** 组件路径 */
    component: string
    /** 菜单标题 */
    title: string
    /** 菜单图标 */
    icon: string
    /** 菜单类型（0目录 1菜单） */
    menuType: number
    /** 排序号 */
    sort: number
    /** 是否隐藏菜单（0否 1是） */
    isHide: number
    /** 是否隐藏标签页（0否 1是） */
    isHideTab: number
    /** 是否缓存（0否 1是） */
    keepAlive: number
    /** 是否iframe（0否 1是） */
    isIframe: number
    /** 外部链接地址 */
    link: string
    /** 是否全屏页面（0否 1是） */
    isFullPage: number
    /** 是否固定标签页（0否 1是） */
    fixedTab: number
    /** 激活菜单路径 */
    activePath: string
    /** 是否显示徽章 */
    showBadge: number
    /** 文本徽章内容 */
    showTextBadge: string
    /** 权限标识 */
    permission: string
    /** 状态（0禁用 1启用） */
    status: number
    /** 备注 */
    remark: string
    /** 创建时间 */
    createTime: string
    /** 更新时间 */
    updateTime: string
    /** 子菜单列表 */
    children?: MenuVO[]
  }

  /**
   * 菜单DTO类型（请求参数）
   */
  interface MenuDTO {
    /** 主键 */
    id?: number
    /** 父菜单ID */
    parentId?: number
    /** 路由名称 */
    name?: string
    /** 路由路径 */
    path?: string
    /** 组件路径 */
    component?: string
    /** 菜单标题 */
    title?: string
    /** 菜单图标 */
    icon?: string
    /** 菜单类型（0目录 1菜单 2按钮） */
    menuType?: number
    /** 排序号 */
    sort?: number
    /** 是否隐藏菜单（0否 1是） */
    isHide?: number
    /** 是否隐藏标签页（0否 1是） */
    isHideTab?: number
    /** 是否缓存（0否 1是） */
    keepAlive?: number
    /** 是否iframe（0否 1是） */
    isIframe?: number
    /** 外部链接地址 */
    link?: string
    /** 是否全屏页面（0否 1是） */
    isFullPage?: number
    /** 是否固定标签页（0否 1是） */
    fixedTab?: number
    /** 激活菜单路径 */
    activePath?: string
    /** 是否显示徽章 */
    showBadge?: number
    /** 文本徽章内容 */
    showTextBadge?: string
    /** 权限标识 */
    permission?: string
    /** 状态（0禁用 1启用） */
    status?: number
    /** 备注 */
    remark?: string
  }

  /**
   * 菜单排序项
   */
  interface MenuSortItem {
    /** 菜单ID */
    id: number
    /** 父级菜单ID */
    parentId: number
    /** 排序号 */
    sort: number
    /** 路由路径（层级变化时需要修正） */
    path?: string
    /** 组件路径（层级变化时需要修正） */
    component?: string
  }

  /**
   * 菜单排序请求参数
   */
  interface MenuSortDTO {
    /** 排序项列表 */
    items: MenuSortItem[]
  }
}
