import { http } from '@/utils/request';

/**
 * 登录参数
 */
export interface LoginParams {
  captchaId: string;
  username: string;
  password: string;
  code: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

/**
 * 用户信息
 */
export interface UserInfo {
  userId: number;
  userName: string;
  nickName: string;
  email?: string;
  avatar?: string;
  permissions?: string[];
  roles?: string[];
  depts?: number[];
}

/**
 * 验证码响应
 */
export interface CaptchaResponse {
  captchaId: string;
  image: string;
}

/**
 * 账号密码登录
 */
export function fetchLogin(params: LoginParams) {
  return http.post<LoginResponse>('/auth/login', params);
}

/**
 * 获取验证码
 */
export function fetchGetCaptcha() {
  return http.get<CaptchaResponse>('/captcha/image');
}

/**
 * 获取用户信息
 */
export function fetchGetUserInfo() {
  return http.get<UserInfo>('/auth/info');
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
  return http.post<LoginResponse>('/auth/refresh', { refreshToken });
}

