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
 * 飞书登录配置 VO
 */
export interface FeishuConfigVO {
  appId: string
  redirectUri: string
  state: string
}

/**
 * 飞书登录/绑定响应
 */
export interface FeishuLoginResponse {
  accessToken?: string
  refreshToken?: string
  needBind?: boolean
  code?: string
  feishuUserId?: string
}

/**
 * 绑定飞书账号参数
 */
export interface BindFeishuAccountParams {
  username: string
  password: string
  code: string
  captchaId: string
  captchaCode: string
}

/**
 * 获取飞书授权URL
 */
export function fetchGetFeishuAuthUrl() {
  return request.get<ThirdAuthUrlVO>({
    url: '/auth/feishu/authorize-url'
  })
}

/**
 * 获取飞书登录配置
 */
export function fetchGetFeishuLoginConfig() {
  return request.get<FeishuConfigVO>({
    url: '/auth/feishu/login-config'
  })
}

/**
 * 飞书OAuth回调处理
 */
export function fetchFeishuCallback(code: string) {
  return request.get<FeishuLoginResponse>({
    url: '/auth/feishu/callback',
    params: { code }
  })
}

/**
 * 绑定飞书账号
 */
export function fetchBindFeishuAccount(params: BindFeishuAccountParams) {
  return request.post<FeishuLoginResponse>({
    url: '/auth/feishu/bind',
    params
  })
}

