import { http } from '@/utils/request';

/**
 * 获取企业微信授权URL
 */
export function fetchGetWeWorkAuthUrl() {
  return http.get<Api.Oauth.ThirdAuthUrlVO>('/auth/wework/authorize-url');
}

/**
 * 获取企业微信登录配置
 */
export function fetchGetWeWorkLoginConfig() {
  return http.get<Api.Oauth.WeWorkConfigVO>('/auth/wework/login-config');
}

/**
 * 企业微信OAuth回调处理
 */
export function fetchWeWorkCallback(code: string) {
  return http.get<Api.Oauth.WeWorkLoginResponse>('/auth/wework/callback', {
    params: { code }
  });
}

/**
 * 绑定企业微信账号
 */
export function fetchBindWeWorkAccount(params: Api.Oauth.BindWeWorkAccountParams) {
  return http.post<Api.Oauth.WeWorkLoginResponse>('/auth/wework/bind', params);
}

