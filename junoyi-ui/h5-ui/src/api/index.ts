import { http } from '@/utils/request';

/**
 * 账号密码登录 (Mock - 用于测试)
 * @returns UseAxiosReturn
 */
export function loginPassword() {
  return http.post(`/mock-api/login`, {
    data: { name: '123' },
  });
}

// 导出新的认证 API（推荐使用）
export * from './auth';
export * as wework from './oauth/wework';
export * as feishu from './oauth/feishu';
export * as dingtalk from './oauth/dingtalk';
