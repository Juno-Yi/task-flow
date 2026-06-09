import { http } from '@/utils/request';

/**
 * 第三方授权 URL VO
 */
export interface ThirdAuthUrlVO {
  authUrl: string;
  authType: string;
  state: string;
}

/**
 * 企业微信登录配置 VO
 */
export interface WeWorkConfigVO {
  corpId: string;
  agentId: string;
  redirectUri: string;
  state: string;
}

/**
 * 企业微信登录/绑定响应
 */
export interface WeWorkLoginResponse {
  accessToken?: string;
  refreshToken?: string;
  needBind?: boolean;
  code?: string;
  weWorkUserId?: string;
}

/**
 * 绑定企业微信账号参数
 */
export interface BindWeWorkAccountParams {
  username: string;
  password: string;
  code: string;
  captchaId: string;
  captchaCode: string;
}

/**
 * 获取企业微信授权URL
 */
export function fetchGetWeWorkAuthUrl() {
  return http.get<ThirdAuthUrlVO>('/auth/wework/authorize-url');
}

/**
 * 获取企业微信登录配置
 */
export function fetchGetWeWorkLoginConfig() {
  return http.get<WeWorkConfigVO>('/auth/wework/login-config');
}

/**
 * 企业微信OAuth回调处理
 */
export function fetchWeWorkCallback(code: string) {
  return http.get<WeWorkLoginResponse>('/auth/wework/callback', {
    params: { code }
  });
}

/**
 * 绑定企业微信账号
 */
export function fetchBindWeWorkAccount(params: BindWeWorkAccountParams) {
  return http.post<WeWorkLoginResponse>('/auth/wework/bind', params);
}

