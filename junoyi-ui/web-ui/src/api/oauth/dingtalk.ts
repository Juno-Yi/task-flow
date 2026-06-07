import request from '@/utils/http'

/**
 * 第三方授权 URL VO
 */
export interface ThirdAuthUrlVO {
  authUrl: string
  authType: string
  state: string
}

/**
 * 钉钉登录配置 VO
 */
export interface DingtalkConfigVO {
  appId: string
  redirectUri: string
  state: string
}

/**
 * 钉钉登录/绑定响应
 */
export interface DingtalkLoginResponse {
  accessToken?: string
  refreshToken?: string
  needBind?: boolean
  code?: string
  dingtalkUserId?: string
}

/**
 * 绑定钉钉账号参数
 */
export interface BindDingtalkAccountParams {
  username: string
  password: string
  code: string
  captchaId: string
  captchaCode: string
}

/**
 * 获取钉钉授权URL
 */
export function fetchGetDingtalkAuthUrl() {
  return request.get<ThirdAuthUrlVO>({
    url: '/auth/dingtalk/authorize-url'
  })
}

/**
 * 获取钉钉登录配置
 */
export function fetchGetDingtalkLoginConfig() {
  return request.get<DingtalkConfigVO>({
    url: '/auth/dingtalk/login-config'
  })
}

/**
 * 钉钉OAuth回调处理
 */
export function fetchDingtalkCallback(code: string) {
  return request.get<DingtalkLoginResponse>({
    url: '/auth/dingtalk/callback',
    params: { code }
  })
}

/**
 * 绑定钉钉账号
 */
export function fetchBindDingtalkAccount(params: BindDingtalkAccountParams) {
  return request.post<DingtalkLoginResponse>({
    url: '/auth/dingtalk/bind',
    params
  })
}

