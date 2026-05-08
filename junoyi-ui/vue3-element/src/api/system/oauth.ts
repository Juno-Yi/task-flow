import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 分页查询OAuth配置列表
 * @param params 查询参数
 * @returns OAuth配置分页结果
 */
export function fetchGetOauthConfigList(params?: Api.Oauth.OauthConfigQueryParams) {
  return request.get<PageResult<Api.Oauth.OauthConfigVO>>({
    url: '/oauth/config/list',
    params
  })
}

/**
 * 新增OAuth配置
 * @param data OAuth配置数据
 */
export function fetchAddOauthConfig(data: Api.Oauth.OauthConfigForm) {
  return request.post({
    url: '/oauth/config',
    data
  })
}

/**
 * 更新OAuth配置
 * @param data OAuth配置数据
 */
export function fetchUpdateOauthConfig(data: Api.Oauth.OauthConfigForm) {
  return request.put({
    url: '/oauth/config',
    data
  })
}

/**
 * 删除OAuth配置
 * @param id 配置ID
 */
export function fetchDeleteOauthConfig(id: number) {
  return request.del({
    url: `/oauth/config/${id}`
  })
}

/**
 * 批量删除OAuth配置
 * @param ids 配置ID数组
 */
export function fetchDeleteOauthConfigBatch(ids: number[]) {
  return request.del({
    url: '/oauth/config/batch',
    data: ids
  })
}

