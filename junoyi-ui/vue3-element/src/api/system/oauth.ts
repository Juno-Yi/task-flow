import request from '@/utils/http'

/**
 * 分页查询OAuth配置列表
 * @param params 查询参数
 * @returns OAuth配置分页结果
 */
export function fetchGetOauthConfigList(params?: Api.Oauth.OauthConfigQueryParams) {
  return request.get<Api.Common.PageResult<Api.Oauth.OauthConfigVO>>({
    url: '/oauth/config/list',
    params
  })
}

