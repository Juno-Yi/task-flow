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
 * 企业微信登录配置 VO
 */
export interface WeWorkConfigVO {
  corpId: string
  agentId: string
  redirectUri: string
  state: string
}

/**
 * 企业微信登录/绑定响应
 */
export interface WeWorkLoginResponse {
  accessToken?: string
  refreshToken?: string
  needBind?: boolean
  code?: string
  weWorkUserId?: string
}

/**
 * 获取企业微信授权URL
 */
export function fetchGetWeWorkAuthUrl() {
  return request.get<ThirdAuthUrlVO>({
    url: '/auth/wework/authorize-url'
  })
}

/**
 * 获取企业微信登录配置
 */
export function fetchGetWeWorkLoginConfig() {
  return request.get<WeWorkConfigVO>({
    url: '/auth/wework/login-config'
  })
}

/**
 * 企业微信OAuth回调处理
 */
export function fetchWeWorkCallback(code: string) {
  return request.get<WeWorkLoginResponse>({
    url: '/auth/wework/callback',
    params: { code }
  })
}

/**
 * 绑定企业微信账号
 */
export function fetchBindWeWorkAccount(username: string, password: string, code: string) {
  // 使用 URLSearchParams 构造表单数据
  const formData = new URLSearchParams()
  formData.append('username', username)
  formData.append('password', password)
  formData.append('code', code)

  return request.post<WeWorkLoginResponse>({
    url: '/auth/wework/bind',
    data: formData,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
