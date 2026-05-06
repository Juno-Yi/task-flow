import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 查询所有字典类型
 */
export function fetchGetAllDictTypes() {
    return request.get<Api.System.DictTypeVO[]>({
        url: '/system/dict/type/all'
    })
}

/**
 * 根据ID查询字典类型详情
 */
export function fetchGetDictTypeById(dictId: number | string) {
    return request.get<Api.System.DictTypeVO>({
        url: `/system/dict/type/${dictId}`
    })
}

/**
 * 新增字典类型
 */
export function fetchAddDictType(data: Api.System.DictTypeDTO) {
    return request.post<void>({
        url: '/system/dict/type',
        data,
    })
}

/**
 * 修改字典类型
 */
export function fetchUpdateDictType(data: Api.System.DictTypeDTO) {
    return request.put<void>({
        url: '/system/dict/type',
        data,
    })
}

/**
 * 删除字典类型
 */
export function fetchDeleteDictType(dictId: number | string) {
    return request.del<void>({
        url: `/system/dict/type/${dictId}`,
    })
}

/**
 * 批量删除字典类型
 */
export function fetchDeleteDictTypes(dictIds: number[]) {
    return request.del<void>({
        url: '/system/dict/type/batch',
        data: dictIds,
    })
}

/**
 * 分页查询字典数据列表
 */
export function fetchGetDictDataList(params?: Api.System.DictDataQueryDTO) {
    return request.get<PageResult<Api.System.DictDataVO>>({
        url: '/system/dict/data/list',
        params
    })
}

/**
 * 根据字典类型查询字典数据
 */
export function fetchGetDictDataByType(dictType: string) {
    return request.get<Api.System.DictDataVO[]>({
        url: `/system/dict/data/type/${dictType}`
    })
}

/**
 * 根据ID查询字典数据详情
 */
export function fetchGetDictDataById(dictCode: number | string) {
    return request.get<Api.System.DictDataVO>({
        url: `/system/dict/data/${dictCode}`
    })
}

/**
 * 新增字典数据
 */
export function fetchAddDictData(data: Api.System.DictDataDTO) {
    return request.post<void>({
        url: '/system/dict/data',
        data,
    })
}

/**
 * 修改字典数据
 */
export function fetchUpdateDictData(data: Api.System.DictDataDTO) {
    return request.put<void>({
        url: '/system/dict/data',
        data,
    })
}

/**
 * 删除字典数据
 */
export function fetchDeleteDictData(dictCode: number | string) {
    return request.del<void>({
        url: `/system/dict/data/${dictCode}`,
    })
}

/**
 * 批量删除字典数据
 */
export function fetchDeleteDictDataList(dictCodes: number[]) {
    return request.del<void>({
        url: '/system/dict/data/batch',
        data: dictCodes,
    })
}
