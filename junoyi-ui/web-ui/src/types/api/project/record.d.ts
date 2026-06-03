/**
 * 项目动态记录相关类型定义
 */
declare namespace Api.Project {
  /**
   * 项目动态记录查询参数
   */
  interface ProjectRecordQueryDTO {
    /** 项目编号（可选，用于筛选） */
    projectNo?: string
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 项目动态记录 VO
   */
  interface ProjectRecordVO {
    /** ID主键 */
    id: number
    /** 项目ID */
    projectId: number
    /** 项目标题 */
    projectTitle: string
    /** 操作者ID */
    operatorId: number
    /** 操作者昵称 */
    operatorNickName: string
    /** 操作类型 */
    type: number
    /** 操作类型标签 */
    typeLabel: string
    /** 操作目标类型 */
    targetType: number
    /** 操作目标类型标签 */
    targetTypeLabel: string
    /** 操作目标ID */
    targetId?: number
    /** 操作内容 */
    content: string
    /** 创建时间 */
    createTime: string
  }
}

