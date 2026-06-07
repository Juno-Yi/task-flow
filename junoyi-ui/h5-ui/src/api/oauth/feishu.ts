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
 * 飞书登录配置 VO
 */
export interface FeishuConfigVO {
  appId: string;
  redirectUri: string;
  state: string;
}

/**
 * 飞书登录/绑定响应
 */
export interface FeishuLoginResponse {
  accessToken?: string;
  refreshToken?: string;
  needBind?: boolean;
  code?: string;
  feishuUserId?: string;
}

/**
 * 绑定飞书账号参数
 */
export interface BindFeishuAccountParams {
  username: string;
  password: string;
  code: string;
  captchaId: string;
  captchaCode: string;
}

/**
 * 获取飞书授权URL
 */
export function fetchGetFeishuAuthUrl() {
  return http.get<ThirdAuthUrlVO>('/auth/feishu/authorize-url');
}

/**
 * 获取飞书登录配置
 */
export function fetchGetFeishuLoginConfig() {
  return http.get<FeishuConfigVO>('/auth/feishu/login-config');
}

/**
 * 飞书OAuth回调处理
 */
export function fetchFeishuCallback(code: string) {
  return http.get<FeishuLoginResponse>('/auth/feishu/callback', {
    params: { code }
  });
}

/**
 * 绑定飞书账号
 */
export function fetchBindFeishuAccount(params: BindFeishuAccountParams) {
  return http.post<FeishuLoginResponse>('/auth/feishu/bind', params);
}

