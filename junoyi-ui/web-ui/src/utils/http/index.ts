/**
 * HTTP 请求封装模块
 * 基于 Axios 封装的 HTTP 请求工具，提供统一的请求/响应处理
 *
 * ## 主要功能
 *
 * - 请求/响应拦截器（自动添加 Token、统一错误处理）
 * - 401 未授权自动刷新 Token（带防抖机制）
 * - 请求失败自动重试（可配置）
 * - 统一的成功/错误消息提示
 * - 支持 GET/POST/PUT/DELETE 等常用方法
 *
 * @module utils/http
 * @author Art Design Pro Team
 */

import axios, { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/store/modules/user'
import { ApiStatus } from './status'
import { HttpError, handleError, showError, showSuccess } from './error'
import { $t } from '@/locales'
import { BaseResponse } from '@/types'
import { encryptRequest, decryptResponse, isApiEncryptEnabled } from './crypto'

/** 请求配置常量 */
const REQUEST_TIMEOUT = 15000
const LOGOUT_DELAY = 500
const MAX_RETRIES = 0
const RETRY_DELAY = 1000
const UNAUTHORIZED_DEBOUNCE_TIME = 3000

/** 401防抖状态 */
let isUnauthorizedErrorShown = false
let unauthorizedTimer: NodeJS.Timeout | null = null

/** Token 刷新状态 */
let isRefreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

/** 扩展 AxiosRequestConfig */
interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  showErrorMessage?: boolean
  showSuccessMessage?: boolean
  noEncrypt?: boolean // 禁用加密（针对单个请求）
  _retry?: boolean // 标记是否为重试请求
}

const { VITE_API_URL, VITE_API_PREFIX, VITE_WITH_CREDENTIALS } = import.meta.env

/** 计算完整的 API 基础路径 */
const getBaseURL = (): string => {
  const apiUrl = (VITE_API_URL || '').trim()
  const apiPrefix = (VITE_API_PREFIX || '').trim()
  // 拼接 URL 和前缀，避免重复斜杠
  if (!apiPrefix) return apiUrl
  const normalizedUrl = apiUrl.endsWith('/') ? apiUrl.slice(0, -1) : apiUrl
  const normalizedPrefix = apiPrefix.startsWith('/') ? apiPrefix : `/${apiPrefix}`
  return `${normalizedUrl}${normalizedPrefix}`
}

/** Axios实例 */
const axiosInstance = axios.create({
  timeout: REQUEST_TIMEOUT,
  baseURL: getBaseURL(),
  withCredentials: VITE_WITH_CREDENTIALS === 'true',
  validateStatus: (status) => status >= 200 && status < 300,
  transformResponse: [
    (data, headers) => {
      // 当开启加密时，自动解密响应
      if (isApiEncryptEnabled() && typeof data === 'string' && data.length > 0) {
        try {
          const decryptedData = decryptResponse(data)
          return JSON.parse(decryptedData)
        } catch (e) {
          console.error('响应解密失败:', e)
          // 解密失败，尝试作为普通 JSON 解析
        }
      }
      
      const contentType = headers['content-type']
      if (contentType?.includes('application/json')) {
        try {
          return JSON.parse(data)
        } catch {
          return data
        }
      }
      return data
    }
  ]
})

/** 请求拦截器 */
axiosInstance.interceptors.request.use(
  (request: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    const accessToken = userStore.accessToken
    if (accessToken) {
      const token = accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`
      request.headers.set('Authorization', token)
    }

    // 获取扩展配置
    const extConfig = request as InternalAxiosRequestConfig & ExtendedAxiosRequestConfig

    // POST/PUT/PATCH/DELETE 请求：如果 params 有数据但 data 为空，自动转换到 data
    // 这个转换必须在加密之前执行
    if (
      ['POST', 'PUT', 'PATCH', 'DELETE'].includes(request.method?.toUpperCase() || '') &&
      extConfig.params &&
      !request.data
    ) {
      request.data = extConfig.params
      extConfig.params = undefined
    }

    if (request.data && !(request.data instanceof FormData) && !request.headers['Content-Type']) {
      request.headers.set('Content-Type', 'application/json')
      
      // 加密请求数据
      if (isApiEncryptEnabled() && !extConfig.noEncrypt) {
        const jsonData = typeof request.data === 'string' ? request.data : JSON.stringify(request.data)
        request.data = encryptRequest(jsonData)
        request.headers.set('X-Encrypted', 'true')
        request.headers.set('Content-Type', 'text/plain')
      } else {
        request.data = typeof request.data === 'string' ? request.data : JSON.stringify(request.data)
      }
    }

    return request
  },
  (error) => {
    showError(createHttpError($t('httpMsg.requestConfigError'), ApiStatus.error))
    return Promise.reject(error)
  }
)

/**
 * 尝试刷新 Token
 * @param originalRequest 原始请求配置
 * @returns 刷新成功后重试的请求 Promise
 */
async function tryRefreshToken(originalRequest: ExtendedAxiosRequestConfig): Promise<AxiosResponse | null> {
  const userStore = useUserStore()
  const currentRefreshToken = userStore.refreshToken

  if (!currentRefreshToken) return null

  // 如果正在刷新，将请求加入队列等待
  if (isRefreshing) {
    return new Promise((resolve) => {
      refreshSubscribers.push((newToken: string) => {
        originalRequest.headers = originalRequest.headers || {}
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`
        resolve(axiosInstance.request(originalRequest))
      })
    })
  }

  originalRequest._retry = true
  isRefreshing = true

  try {
    const response = await axios.post(
      `${getBaseURL()}/auth/refresh`,
      null,
      { params: { refreshToken: currentRefreshToken } }
    )

    // 解密响应数据（如果启用了加密）
    let responseData = response.data
    if (isApiEncryptEnabled() && typeof responseData === 'string' && responseData.length > 0) {
      try {
        const decryptedData = decryptResponse(responseData)
        responseData = JSON.parse(decryptedData)
      } catch (e) {
        console.error('刷新Token响应解密失败:', e)
      }
    }

    if (responseData.code !== ApiStatus.success) {
      throw new Error(responseData.msg || responseData.message || 'Token 刷新失败')
    }

    const { accessToken, refreshToken } = responseData.data
    userStore.setToken(accessToken, refreshToken)

    // 通知所有等待的请求
    refreshSubscribers.forEach((callback) => callback(accessToken))
    refreshSubscribers = []

    // 重试原请求
    originalRequest.headers = originalRequest.headers || {}
    originalRequest.headers['Authorization'] = `Bearer ${accessToken}`
    return axiosInstance.request(originalRequest)
  } catch {
    refreshSubscribers = []
    return null
  } finally {
    isRefreshing = false
  }
}

/** 响应拦截器 */
axiosInstance.interceptors.response.use(
  async (response: AxiosResponse<BaseResponse>) => {
    const { code, msg, message } = response.data
    const responseMsg = msg || message

    if (code === ApiStatus.success) return response

    // 处理业务层面的 401
    if (code === ApiStatus.unauthorized) {
      const originalRequest = response.config as ExtendedAxiosRequestConfig
      if (!originalRequest._retry) {
        const retryResponse = await tryRefreshToken(originalRequest)
        if (retryResponse) return retryResponse
        handleUnauthorizedError(responseMsg)
      }
      throw createHttpError(responseMsg || $t('httpMsg.unauthorized'), code)
    }

    throw createHttpError(responseMsg || $t('httpMsg.requestFailed'), code)
  },
  async (error) => {
    const originalRequest = error.config as ExtendedAxiosRequestConfig
    const httpStatus = error.response?.status
    const errorMsg = error.response?.data?.msg || error.response?.data?.message

    // 处理 HTTP 401
    if (httpStatus === ApiStatus.unauthorized && !originalRequest._retry) {
      const retryResponse = await tryRefreshToken(originalRequest)
      if (retryResponse) return retryResponse
      handleUnauthorizedError(errorMsg)
    }

    return Promise.reject(handleError(error))
  }
)

/** 统一创建HttpError */
function createHttpError(message: string, code: number) {
  return new HttpError(message, code)
}

/** 处理401错误（带防抖） */
function handleUnauthorizedError(message?: string): void {
  if (!isUnauthorizedErrorShown) {
    isUnauthorizedErrorShown = true
    const error = createHttpError(message || $t('httpMsg.unauthorized'), ApiStatus.unauthorized)
    showError(error, true)
    logOut()
    unauthorizedTimer = setTimeout(resetUnauthorizedError, UNAUTHORIZED_DEBOUNCE_TIME)
  }
}

/** 重置401防抖状态 */
function resetUnauthorizedError() {
  isUnauthorizedErrorShown = false
  if (unauthorizedTimer) clearTimeout(unauthorizedTimer)
  unauthorizedTimer = null
}

/** 退出登录函数 */
function logOut() {
  setTimeout(() => {
    useUserStore().logOut()
  }, LOGOUT_DELAY)
}

/** 是否需要重试 */
function shouldRetry(statusCode: number) {
  return [
    ApiStatus.requestTimeout,
    ApiStatus.internalServerError,
    ApiStatus.badGateway,
    ApiStatus.serviceUnavailable,
    ApiStatus.gatewayTimeout
  ].includes(statusCode)
}

/** 请求重试逻辑 */
async function retryRequest<T>(
  config: ExtendedAxiosRequestConfig,
  retries: number = MAX_RETRIES
): Promise<T> {
  try {
    return await request<T>(config)
  } catch (error) {
    if (retries > 0 && error instanceof HttpError && shouldRetry(error.code)) {
      await delay(RETRY_DELAY)
      return retryRequest<T>(config, retries - 1)
    }
    throw error
  }
}

/** 延迟函数 */
function delay(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

/** 请求函数 */
async function request<T = any>(config: ExtendedAxiosRequestConfig): Promise<T> {
  try {
    const res = await axiosInstance.request<BaseResponse<T>>(config)

    // 显示成功消息
    const successMsg = res.data.msg || res.data.message
    if (config.showSuccessMessage && successMsg) {
      showSuccess(successMsg)
    }

    return res.data.data as T
  } catch (error) {
    if (error instanceof HttpError && error.code !== ApiStatus.unauthorized) {
      const showMsg = config.showErrorMessage !== false
      showError(error, showMsg)
    }
    return Promise.reject(error)
  }
}

/** API方法集合 */
const api = {
  get<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'GET' })
  },
  post<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'POST' })
  },
  put<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'PUT' })
  },
  del<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'DELETE' })
  },
  request<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>(config)
  }
}

export default api
