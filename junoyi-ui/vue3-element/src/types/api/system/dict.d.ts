/**
 * 字典管理相关类型定义
 */
declare namespace Api.System {
    /**
     * 字典类型查询参数
     */
    interface DictTypeQueryDTO {
        /** 字典名称 */
        dictName?: string
        /** 字典类型 */
        dictType?: string
        /** 状态（0正常 1停用） */
        status?: string
        /** 页码 */
        pageNum?: number
        /** 每页数量 */
        pageSize?: number
    }

    /**
     * 字典类型VO（后端返回格式）
     */
    interface DictTypeVO {
        /** 字典主键 */
        dictId: number
        /** 字典名称 */
        dictName: string
        /** 字典类型 */
        dictType: string
        /** 状态（0正常 1停用） */
        status: string
        /** 创建时间 */
        createTime: string
        /** 更新时间 */
        updateTime: string
        /** 备注 */
        remark: string
    }

    /**
     * 字典类型DTO（请求参数）
     */
    interface DictTypeDTO {
        /** 字典主键（修改时必填） */
        dictId?: number
        /** 字典名称 */
        dictName?: string
        /** 字典类型 */
        dictType?: string
        /** 状态（0正常 1停用） */
        status?: string
        /** 备注 */
        remark?: string
    }

    /**
     * 字典数据查询参数
     */
    interface DictDataQueryDTO {
        /** 字典类型 */
        dictType?: string
        /** 字典标签 */
        dictLabel?: string
        /** 状态（0正常 1停用） */
        status?: string
        /** 当前页码 */
        current?: number
        /** 每页数量 */
        size?: number
    }

    /**
     * 字典数据VO（后端返回格式）
     */
    interface DictDataVO {
        /** 字典编码 */
        dictCode: number
        /** 字典排序 */
        dictSort: number
        /** 字典标签 */
        dictLabel: string
        /** 字典键值 */
        dictValue: string
        /** 字典类型 */
        dictType: string
        /** 样式属性 */
        cssClass: string
        /** 表格回显样式 */
        listClass: string
        /** 是否默认（Y是 N否） */
        isDefault: string
        /** 状态（0正常 1停用） */
        status: string
        /** 创建时间 */
        createTime: string
        /** 更新时间 */
        updateTime: string
        /** 备注 */
        remark: string
    }

    /**
     * 字典数据DTO（请求参数）
     */
    interface DictDataDTO {
        /** 字典编码（修改时必填） */
        dictCode?: number
        /** 字典排序 */
        dictSort?: number
        /** 字典标签 */
        dictLabel?: string
        /** 字典键值 */
        dictValue?: string
        /** 字典类型 */
        dictType?: string
        /** 样式属性 */
        cssClass?: string
        /** 表格回显样式 */
        listClass?: string
        /** 是否默认（Y是 N否） */
        isDefault?: string
        /** 状态（0正常 1停用） */
        status?: string
        /** 备注 */
        remark?: string
    }
}
