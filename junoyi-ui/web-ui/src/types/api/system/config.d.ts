/**
 * 系统参数配置相关类型定义
 */
declare namespace Api.System {
  /**
   * 系统参数查询参数
   */
  interface ConfigQueryDTO {
    /** 参数名称 */
    configName?: string
    /** 参数键名 */
    configKey?: string
    /** 参数类型（text/number/boolean/json） */
    configType?: string
    /** 系统内置（Y是 N否） */
    isSystem?: string
    /** 当前页 */
    current?: number
    /** 每页条数 */
    size?: number
  }

  /**
   * 系统参数VO类型（后端返回格式）
   */
  interface ConfigVO {
    /** 参数ID */
    id: number
    /** 参数键名 */
    configKey: string
    /** 参数键值 */
    configValue: string
    /** 参数名称 */
    configName: string
    /** 参数类型（text/number/boolean/json） */
    configType: string
    /** 排序 */
    sort?: number
    /** 系统内置（Y是 N否） */
    isSystem: string
    /** 状态（0正常 1停用） */
    status?: number
    /** 备注 */
    remark?: string
    /** 创建时间 */
    createTime?: string
    /** 更新时间 */
    updateTime?: string
  }

  /**
   * 系统参数DTO类型（请求参数）
   */
  interface ConfigDTO {
    /** 参数ID（修改时必填） */
    id?: number
    /** 参数键名 */
    configKey: string
    /** 参数键值 */
    configValue: string
    /** 参数名称 */
    configName: string
    /** 参数类型（text/number/boolean/json） */
    configType: string
    /** 排序 */
    sort?: number
    /** 系统内置（Y是 N否） */
    isSystem?: string
    /** 状态（0正常 1停用） */
    status?: number
    /** 备注 */
    remark?: string
  }
}
