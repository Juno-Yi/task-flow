import axios from 'axios';
import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { showToast } from 'vant';
import { useUserStore } from '@/store/modules/user';
import { fetchRefreshToken } from '@/api/auth';

// 是否正在刷新 token
let isRefreshing = false;
// 刷新 token 失败的请求队列
let requestQueue: Array<() => void> = [];

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  withCredentials: false,
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
        // 刷新 token
        const { accessToken, refreshToken } = await fetchRefreshToken(userStore.refreshToken);
        userStore.setToken(accessToken, refreshToken);

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
