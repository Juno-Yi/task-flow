import request from '@/utils/request';

/**
 * 获取企业微信授权URL
 * @returns 授权URL信息
 */
export function fetchGetWeWorkAuthUrl() {
  return request.get<Api.Oauth.ThirdAuthUrlVO>({
    url: '/auth/wework/authorize-url'
  });
}

/**
 * 获取企业微信登录配置
 * @returns 企业微信配置
 */
export function fetchGetWeWorkLoginConfig() {
  return request.get<Api.Oauth.WeWorkConfigVO>({
    url: '/auth/wework/login-config'
  });
}

/**
 * 企业微信OAuth回调处理
 * @param code 授权码
 * @returns 登录响应
 */
export function fetchWeWorkCallback(code: string) {
  return request.get<Api.Oauth.WeWorkLoginResponse>({
    url: '/auth/wework/callback',
    params: { code }
  });
}

/**
 * 绑定企业微信账号
 * @param params 绑定参数
 * @returns 登录响应
 */
export function fetchBindWeWorkAccount(params: Api.Oauth.BindWeWorkAccountParams) {
  return request.post<Api.Oauth.WeWorkLoginResponse>({
    url: '/auth/wework/bind',
    data: params
  });
}

