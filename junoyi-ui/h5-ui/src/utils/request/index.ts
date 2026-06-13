import axios from 'axios';
import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { showToast } from 'vant';
import { useUserStore } from '@/store/modules/user';
import router from '@/router';
import { encryptRequest, decryptResponse, isApiEncryptEnabled } from './crypto';

/** 请求配置常量 */
const REQUEST_TIMEOUT = 15000;
const LOGOUT_DELAY = 500;
const UNAUTHORIZED_DEBOUNCE_TIME = 3000;

/** 401 防抖状态 */
let isUnauthorizedErrorShown = false;
let unauthorizedTimer: NodeJS.Timeout | null = null;

/** Token 刷新状态 */
let isRefreshing = false;
let refreshSubscribers: Array<(token: string) => void> = [];

/** 扩展 AxiosRequestConfig */
interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  noEncrypt?: boolean; // 禁用加密（针对单个请求）
  _retry?: boolean; // 标记是否为重试请求
}

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

const service: AxiosInstance = axios.create({
  baseURL: getBaseURL(),
  withCredentials: VITE_WITH_CREDENTIALS === 'true',
  timeout: REQUEST_TIMEOUT,
  transformResponse: [
    (data, headers) => {
      // 当开启加密时，自动解密响应
      if (isApiEncryptEnabled() && typeof data === 'string' && data.length > 0) {
        try {
          const decryptedData = decryptResponse(data);
          return JSON.parse(decryptedData);
        } catch (e) {
          console.error('响应解密失败:', e);
          // 解密失败，尝试作为普通 JSON 解析
        }
      }

      const contentType = headers?.['content-type'];
      if (contentType?.includes('application/json')) {
        try {
          return JSON.parse(data);
        } catch {
          return data;
        }
      }
      return data;
    },
  ],
});

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore();
    // 自动添加 accessToken
    if (userStore.accessToken) {
      config.headers.Authorization = `Bearer ${userStore.accessToken}`;
    }

    // 获取扩展配置
    const extConfig = config as InternalAxiosRequestConfig & ExtendedAxiosRequestConfig;

    // POST/PUT/PATCH/DELETE 请求：如果 params 有数据但 data 为空，自动转换到 data
    // 这个转换必须在加密之前执行
    if (
      ['POST', 'PUT', 'PATCH', 'DELETE'].includes(config.method?.toUpperCase() || '') &&
      extConfig.params &&
      !config.data
    ) {
      config.data = extConfig.params;
      extConfig.params = undefined;
    }

    if (config.data && !(config.data instanceof FormData) && !config.headers['Content-Type']) {
      config.headers.set('Content-Type', 'application/json');

      // 加密请求数据
      if (isApiEncryptEnabled() && !extConfig.noEncrypt) {
        const jsonData = typeof config.data === 'string' ? config.data : JSON.stringify(config.data);
        config.data = encryptRequest(jsonData);
        config.headers.set('X-Encrypted', 'true');
        config.headers.set('Content-Type', 'text/plain');
      } else {
        config.data = typeof config.data === 'string' ? config.data : JSON.stringify(config.data);
      }
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

/** 尝试刷新 Token 并重试请求 */
async function tryRefreshToken(originalRequest: ExtendedAxiosRequestConfig): Promise<AxiosResponse | null> {
  originalRequest._retry = true;

  // 如果正在刷新，将请求加入订阅列表
  if (isRefreshing) {
    return new Promise((resolve) => {
      refreshSubscribers.push((token: string) => {
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${token}`;
        resolve(service(originalRequest));
      });
    });
  }

  isRefreshing = true;

  try {
    const userStore = useUserStore();

    // 检查 refreshToken 是否存在
    if (!userStore.refreshToken) {
      console.error('refreshToken 不存在，清除登录状态');
      userStore.clearUser();
      return null;
    }

    console.log('尝试刷新 Token');

    // 动态导入 fetchRefreshToken 避免循环依赖
    const { fetchRefreshToken } = await import('@/api/auth');
    const { accessToken, refreshToken } = await fetchRefreshToken(userStore.refreshToken);

    // 更新 Token
    userStore.setToken(accessToken, refreshToken);

    console.log('Token 刷新成功');

    // 通知所有等待的请求
    refreshSubscribers.forEach((callback) => callback(accessToken));
    refreshSubscribers = [];

    // 重试当前请求
    originalRequest.headers = originalRequest.headers || {};
    originalRequest.headers.Authorization = `Bearer ${accessToken}`;
    return service(originalRequest);
  } catch (error: any) {

    // Token 刷新失败，清除所有登录信息
    const userStore = useUserStore();
    userStore.clearUser();

    // 清空订阅列表
    refreshSubscribers = [];

    return null;
  } finally {
    isRefreshing = false;
  }
}

/** 处理 401 错误（带防抖） */
function handleUnauthorizedError(message?: string): void {
  if (!isUnauthorizedErrorShown) {
    isUnauthorizedErrorShown = true;
    const userStore = useUserStore();

    showToast(message || '登录已过期，请重新登录');

    // 清除用户信息
    userStore.clearUser();

    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login');
    }, LOGOUT_DELAY);

    // 清除之前的定时器
    if (unauthorizedTimer) {
      clearTimeout(unauthorizedTimer);
    }

    // 设置防抖定时器
    unauthorizedTimer = setTimeout(() => {
      isUnauthorizedErrorShown = false;
      unauthorizedTimer = null;
    }, UNAUTHORIZED_DEBOUNCE_TIME);
  }
}

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    // 业务成功
    if (res.code === 200) {
      return res.data;
    }

    // 业务层面的 401
    if (res.code === 401) {
      const originalRequest = response.config as ExtendedAxiosRequestConfig;
      if (!originalRequest._retry) {
        // 尝试刷新 Token
        return tryRefreshToken(originalRequest).then((retryResponse) => {
          if (retryResponse) return retryResponse.data.data;
          handleUnauthorizedError(res.msg);
          return Promise.reject(new Error(res.msg || '未授权'));
        });
      }
    }

    // 其他业务错误
    showToast(res.msg || 'Error');
    return Promise.reject(new Error(res.msg || 'Error'));
  },
  async (error: AxiosError) => {
    const status = error.response?.status;
    const originalRequest = error.config as ExtendedAxiosRequestConfig;
    const errorMsg = (error.response?.data as any)?.msg || error.message;

    // HTTP 401 未授权
    if (status === 401 && originalRequest && !originalRequest._retry) {
      const retryResponse = await tryRefreshToken(originalRequest);
      if (retryResponse) return retryResponse;
      handleUnauthorizedError(errorMsg);
    }

    // 其他错误
    const message = errorMsg || 'Network Error';
    showToast(message);
    return Promise.reject(error);
  },
);

/** 请求函数 */
async function request<T = any>(config: ExtendedAxiosRequestConfig): Promise<T> {
  try {
    const response = await service.request(config);
    return response as T;
  } catch (error) {
    return Promise.reject(error);
  }
}

/** API 方法集合（统一风格：对象参数） */
const api = {
  get<T>(config: ExtendedAxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, method: 'GET' });
  },

  post<T>(config: ExtendedAxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, method: 'POST' });
  },

  put<T>(config: ExtendedAxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, method: 'PUT' });
  },

  delete<T>(config: ExtendedAxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, method: 'DELETE' });
  },

  request<T>(config: ExtendedAxiosRequestConfig): Promise<T> {
    return request<T>(config);
  }
};

export default api;

/** 兼容旧代码的导出（逐步废弃） */
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return api.get<T>({ url, ...config });
  },

  post<T = any>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
    return api.post<T>({ url, data, ...config });
  },

  put<T = any>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
    return api.put<T>({ url, data, ...config });
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return api.delete<T>({ url, ...config });
  },
};
