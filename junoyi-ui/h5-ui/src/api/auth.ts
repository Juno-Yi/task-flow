import { http } from '@/utils/request';

/**
 * 账号密码登录
 */
export function fetchLogin(params: Api.Auth.LoginParams) {
  return http.post<Api.Auth.LoginResponse>('/auth/login', params);
}

/**
 * 获取验证码
 */
export function fetchGetCaptcha() {
  return http.get<Api.Auth.CaptchaResponse>('/captcha/image');
}

/**
 * 获取用户信息
 */
export function fetchGetUserInfo() {
  return http.get<Api.Auth.UserInfo>('/auth/info');
}

/**
 * 退出登录
 */
export function fetchLogout() {
  return http.post<void>('/auth/logout');
}

/**
 * 刷新 Token
 */
export function fetchRefreshToken(refreshToken: string) {
  return http.post<Api.Auth.LoginResponse>('/auth/refresh', { refreshToken });
}

