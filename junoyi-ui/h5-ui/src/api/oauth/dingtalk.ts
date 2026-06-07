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
 * 钉钉登录配置 VO
 */
export interface DingtalkConfigVO {
  appId: string;
  redirectUri: string;
  state: string;
}

/**
 * 钉钉登录/绑定响应
 */
export interface DingtalkLoginResponse {
  accessToken?: string;
  refreshToken?: string;
  needBind?: boolean;
  code?: string;
  dingtalkUserId?: string;
}

/**
 * 绑定钉钉账号参数
 */
export interface BindDingtalkAccountParams {
  username: string;
  password: string;
  code: string;
  captchaId: string;
  captchaCode: string;
}

/**
 * 获取钉钉授权URL
 */
export function fetchGetDingtalkAuthUrl() {
  return http.get<ThirdAuthUrlVO>('/auth/dingtalk/authorize-url');
}

/**
 * 获取钉钉登录配置
 */
export function fetchGetDingtalkLoginConfig() {
  return http.get<DingtalkConfigVO>('/auth/dingtalk/login-config');
}

/**
 * 钉钉OAuth回调处理
 */
export function fetchDingtalkCallback(code: string) {
  return http.get<DingtalkLoginResponse>('/auth/dingtalk/callback', {
    params: { code }
  });
}

/**
 * 绑定钉钉账号
 */
export function fetchBindDingtalkAccount(params: BindDingtalkAccountParams) {
  return http.post<DingtalkLoginResponse>('/auth/dingtalk/bind', params);
}

