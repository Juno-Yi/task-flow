/**
 * 项目仓库相关类型定义
 */
declare namespace Api.Project {
  /**
   * 项目查询参数
   */
  interface ProjectListQueryDTO {
    /** 项目编号 */
    no?: string
    /** 项目名称 */
    name?: string
    /** 项目类型 */
    type?: number
    /** 项目状态 */
    status?: number
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 项目视图对象
   */
  interface ProjectListVO {
    /** 项目ID */
    id: number
    /** 项目编号 */
    no: string
    /** 项目名称 */
    name: string
    /** 项目描述 */
    description?: string
    /** 项目负责人ID */
    leader: number
    /** 项目负责人名称 */
    leaderName?: string
    /** 项目类型 */
    type: number
    /** 项目类型标签（字典翻译） */
    typeLabel?: string
    /** 项目类型标签类型（用于前端标签颜色） */
    typeLabelType?: string
    /** 项目状态 */
    status: number
    /** 项目状态标签（字典翻译） */
    statusLabel?: string
    /** 项目状态标签类型（用于前端标签颜色） */
    statusType?: string
    /** 项目优先级 */
    priority?: number
    /** 项目优先级标签（字典翻译） */
    priorityLabel?: string
    /** 项目优先级标签类型（用于前端标签颜色） */
    priorityType?: string
    /** 项目成员数量 */
    memberCount: number
    /** 项目进度百分比 */
    progress: number
    /** 总任务数量 */
    totalTasks: number
    /** 完成任务数量 */
    completedTasks: number
    /** 创建者 */
    createBy?: string
    /** 创建时间 */
    createTime?: string
    /** 更新者 */
    updateBy?: string
    /** 更新时间 */
    updateTime?: string
    /** 备注 */
    remark?: string
  }

  /**
   * 项目下拉列表VO
   */
  interface ProjectOptionVO {
    id: number
    no: string
    name: string
    status: number
    priority: number
  }

  /**
   * 项目下拉列表查询参数 GTO
   */
  interface ProjectOptionQueryDTO {
    no: string
    name: string
  }

  /**
   * 项目详情视图对象
   */
  interface ProjectDetailVO {
    /** 项目ID */
    id: number
    /** 项目编号 */
    no: string
    /** 项目名称 */
    name: string
    /** 项目描述 */
    description?: string
    /** 项目负责人ID */
    leader: number
    /** 项目负责人名称 */
    leaderName?: string
    /** 项目类型 */
    type: number
    /** 项目类型标签（字典翻译） */
    typeLabel?: string
    /** 项目类型标签类型（用于前端标签颜色） */
    typeLabelType?: string
    /** 项目状态 */
    status: number
    /** 项目状态标签（字典翻译） */
    statusLabel?: string
    /** 项目状态标签类型（用于前端标签颜色） */
    statusType?: string
    /** 项目优先级 */
    priority?: number
    /** 项目优先级标签（字典翻译） */
    priorityLabel?: string
    /** 项目优先级标签类型（用于前端标签颜色） */
    priorityType?: string
    /** 项目成员数量 */
    memberCount: number
    /** 项目进度百分比 */
    progress: number
    /** 总任务数量 */
    totalTasks: number
    /** 完成任务数量 */
    completedTasks: number
    /** 未完成任务数量（待处理、进行中、测试中等） */
    pendingTasks: number
    /** 仓库数量 */
    repositoryCount: number
    /** 文档数量 */
    documentCount: number
    /** 里程碑数量 */
    milestoneCount: number
    /** 创建者 */
    createBy?: string
    /** 创建时间 */
    createTime?: string
    /** 更新者 */
    updateBy?: string
    /** 更新时间 */
    updateTime?: string
    /** 备注 */
    remark?: string
    /** 最近的项目成员 */
    recentMembers?: ProjectMemberVO[]
    /** 当前用户在项目中的角色 */
    currentUserRole?: string | null
  }

  /**
   * 项目信息视图对象（简化版）
   */
  interface ProjectInfoVO {
    /** 项目ID */
    id: number
    /** 项目编号 */
    no: string
    /** 项目名称 */
    name: string
    /** 项目描述 */
    description?: string
    /** 项目类型 */
    type: number
    /** 项目类型标签（字典翻译） */
    typeLabel?: string
    /** 项目类型标签类型（用于前端标签颜色） */
    typeLabelType?: string
  }



  /**
   * 项目传输数据
   */
  interface ProjectListDTO {
    /** 项目ID（修改时必填） */
    id?: number
    /** 项目名称 */
    name: string
    /** 项目描述 */
    description?: string
    /** 项目负责人ID */
    leader: number
    /** 项目类型 */
    type: number
    /** 项目状态（新增时可选，默认为0；修改时必填） */
    status?: number
    /** 项目优先级 */
    priority?: number
    /** 备注 */
    remark?: string
  }
}

declare namespace Api.Project {

  /**
   * 项目恢复 DTO
   */
  interface ProjectRestoreDTO {
    ids: number[]
  }

  interface ProjectDeleteDTO {
    ids: number[]
    password: string
  }

  /**
   * 项目详情视图对象
   */
  interface ProjectDetailVO {
    /** 项目ID */
    id: number
    /** 项目编号 */
    no: string
    /** 项目名称 */
    name: string
    /** 项目描述 */
    description?: string
    /** 项目负责人ID */
    leader: number
    /** 项目负责人名称 */
    leaderName?: string
    /** 项目类型 */
    type: number
    /** 项目类型标签（字典翻译） */
    typeLabel?: string
    /** 项目类型标签类型（用于前端标签颜色） */
    typeLabelType?: string
    /** 项目状态 */
    status: number
    /** 项目状态标签（字典翻译） */
    statusLabel?: string
    /** 项目状态标签类型（用于前端标签颜色） */
    statusType?: string
    /** 项目优先级 */
    priority?: number
    /** 项目优先级标签（字典翻译） */
    priorityLabel?: string
    /** 项目优先级标签类型（用于前端标签颜色） */
    priorityType?: string
    /** 项目成员数量 */
    memberCount: number
    /** 项目进度百分比 */
    progress: number
    /** 总任务数量 */
    totalTasks: number
    /** 完成任务数量 */
    completedTasks: number
    /** 未完成任务数量（待处理、进行中、测试中等） */
    pendingTasks: number
    /** 仓库数量 */
    repositoryCount: number
    /** 文档数量 */
    documentCount: number
    /** 里程碑数量 */
    milestoneCount: number
    /** 创建者 */
    createBy?: string
    /** 创建时间 */
    createTime?: string
    /** 更新者 */
    updateBy?: string
    /** 更新时间 */
    updateTime?: string
    /** 备注 */
    remark?: string
    /** 最近的项目成员 */
    recentMembers?: ProjectMemberVO[]
    /** 当前用户在项目中的角色 */
    currentUserRole?: string | null
  }
}
