import axios from 'axios';
import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { showToast } from 'vant';
import { useUserStore } from '@/store/modules/user';
import { fetchRefreshToken } from '@/api/auth';

// 环境变量
const { VITE_API_URL, VITE_API_PREFIX, VITE_WITH_CREDENTIALS } = import.meta.env;

/** 计算完整的 API 基础路径 */
const getBaseURL = (): string => {
  const apiUrl = (VITE_API_URL || '').trim();
  const apiPrefix = (VITE_API_PREFIX || '').trim();

  // 拼接 API URL 和 prefix
  if (apiUrl.endsWith('/') && apiPrefix.startsWith('/')) {
    return apiUrl + apiPrefix.slice(1);
  }
  if (!apiUrl.endsWith('/') && !apiPrefix.startsWith('/')) {
    return apiUrl + '/' + apiPrefix;
  }
  return apiUrl + apiPrefix;
};

// 是否正在刷新 token
let isRefreshing = false;
// 刷新 token 失败的请求队列
let requestQueue: Array<() => void> = [];

const service: AxiosInstance = axios.create({
  baseURL: getBaseURL(),
  withCredentials: VITE_WITH_CREDENTIALS === 'true',
  timeout: 15000,
});

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore();
    // 使用 accessToken
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;
    if (res.code !== 200) {
      showToast(res.msg || 'Error');
      return Promise.reject(new Error(res.msg || 'Error'));
    }
    return res.data;
  },
  async (error: AxiosError) => {
    const status = error.response?.status;
    const config = error.config as InternalAxiosRequestConfig;

    // 401 未授权 - 尝试刷新 token
    if (status === 401 && config) {
      const userStore = useUserStore();

      // 如果没有 refreshToken，直接跳转登录
      if (!userStore.refreshToken) {
        userStore.clearUser();
        window.location.hash = '#/login';
        return Promise.reject(error);
      }

      // 如果正在刷新 token，将请求加入队列
      if (isRefreshing) {
        return new Promise((resolve) => {
          requestQueue.push(() => {
            config.headers.Authorization = `Bearer ${userStore.accessToken}`;
            resolve(service(config));
          });
        });
      }

      isRefreshing = true;

      try {
        // 检查 refreshToken 是否存在
        if (!userStore.refreshToken) {
          console.error('refreshToken 不存在，清除用户信息');
          userStore.clearUser();
          isRefreshing = false;
          return Promise.reject(error);
        }

        console.log('尝试刷新 Token，refreshToken:', userStore.refreshToken.substring(0, 20) + '...');

        // 刷新 token
        const result = await fetchRefreshToken(userStore.refreshToken);
        console.log('刷新 Token 接口返回:', result);

        const { accessToken, refreshToken } = result;
        userStore.setToken(accessToken, refreshToken);

        console.log('Token 刷新成功');

        // 重试队列中的请求
        requestQueue.forEach((callback) => callback());
        requestQueue = [];

        // 重试当前请求
        config.headers.Authorization = `Bearer ${accessToken}`;
        return service(config);
      } catch (refreshError) {
        // 刷新 token 失败，清空用户信息并跳转登录页
        userStore.clearUser();
        window.location.hash = '#/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    const message = (error.response?.data as any)?.msg || error.message || 'Network Error';
    showToast(message);
    return Promise.reject(error);
  },
);

export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config);
  },

  post<T = any>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config);
  },

  put<T = any>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config);
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config);
  },
};

export default service;
