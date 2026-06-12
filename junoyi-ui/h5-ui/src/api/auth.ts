import request from '@/utils/request';

/**
 * 账号密码登录
 * @param params 登录参数
 * @returns 登录响应
 */
export function fetchLogin(params: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/auth/login',
    data: params
  });
}

/**
 * 获取验证码
 * @returns 验证码响应
 */
export function fetchGetCaptcha() {
  return request.get<Api.Auth.CaptchaResponse>({
    url: '/captcha/image'
  });
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export function fetchGetUserInfo() {
  return request.get<Api.Auth.UserInfo>({
    url: '/auth/info'
  });
}

/**
 * 退出登录
 * @returns 退出响应
 */
export function fetchLogout() {
  return request.post<void>({
    url: '/auth/logout'
  });
}

/**
 * 刷新 Token
 * @param refreshToken 刷新令牌
 * @returns 新的 accessToken 和 refreshToken
 */
export function fetchRefreshToken(refreshToken: string) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/auth/refresh',
    params: { refreshToken }
  });
}

