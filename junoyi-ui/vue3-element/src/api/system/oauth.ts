import request from '@/utils/http'

/**
 * 获取OAuth配置列表
 * @returns OAuth配置列表
 */
export function fetchGetOauthConfigList() {
  return request.get<Api.Oauth.OauthConfigVO[]>({
    url: '/oauth/config/list'
  })
}

